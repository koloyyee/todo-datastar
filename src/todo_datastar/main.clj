;;(refer-clojure :exclude '[filter for group-by into partition-by set update])
(ns todo-datastar.main
  ;; (:gen-class)
  (:require
   [clojure.java.io :as io]
   [hiccup2.core :as hp]
   [org.httpkit.server :as server]
   [reitit.ring]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.util.response :as resp]
   [starfederation.datastar.clojure.adapter.http-kit :as hk-gen]
   [starfederation.datastar.clojure.api :as d*]
   [next.jdbc.connection :as connection]
   [next.jdbc :as jdbc]
   [honey.sql :as sql]
   [todo-datastar.html :as html]
   ;; [todo-datastar.db :refer [items]]
   )
  (:import (com.zaxxer.hikari HikariDataSource)
           (org.flywaydb.core Flyway)
           (java.util.concurrent Executors)))

;; DB
(defonce db-pool
  (delay
    (connection/->pool HikariDataSource
                       {:dbtype "postgres"
                        :dbname "postgres"
                        :host "localhost"
                        :port 5432
                        :user "datastar"
                        :password "postgres"})))
(defn migrate-db! []
  (let [flyway (-> (Flyway/configure)
                   (.dataSource "jdbc:postgresql://localhost:5432/postgres" "datastar" "postgres")
                   (.locations (into-array ["classpath:db/migration"]))
                   (.load))]
    (.migrate flyway)))


;; DB Logic
(defn insert-todo! 
  "Inserting new item to the Todos table"
  [title description]
  (let [query (sql/format
               {:insert-into [:todos]
                :columns [:title :description :done]
                :values [{:title title
                          :description description
                          :done false}]})]
    (jdbc/execute! @db-pool query)))
;;(insert-todo! "carrot" "carrot is on sale buy it!")

(defn select-todos 
 "Returning all items from Todos table with raw SQL" 
  []
  (jdbc/execute! @db-pool ["SELECT * FROM todos"]))
(select-todos)

(defn select-todo 
  "Selecting item by id with HoneySQL"
  [id]
  (let [query (sql/format {:select [:*]
                           :from [:todos]
                           :where [:= :todos.id id]})]
    (jdbc/execute! @db-pool query)))
(select-todo 1)

(defn mark-done! 
"Marking item as done."
  [id]
  (let [query (sql/format {:update  :todos
                           :set {:done true}
                           :where [:= :id id]})]
    (jdbc/execute! @db-pool query)))
(mark-done! 1)

(defn delete-todo!
 "Delete item by id with type hinting in parameter" 
  [^Integer id]
  (let [query (sql/format {:delete-from [:todos]
                           :where [:= :id id]})]
    (jdbc/execute! @db-pool query)))

(delete-todo! 1)


;; Generated HTML in Hiccup style as data
(defn list-item 
"Return a single row of todo item in li"  
  [item]
  [:li {:id (:todos/id item)}
   [:h2.title (:todos/title item)]
   [:p.description (:todos/description item)]
   [:button.done
    {:data-on-click (d*/sse-patch (str "/todos/" (:todos/id item)))
     :disabled (:todos/done item)}
    "Done"]
   [:button.remove    {:data-on-click (d*/sse-delete (str "/todos/" (:todos/id item)))
                       :disabled (not (:todos/done item))}
    "Remove"]])

;; Q: How can i add the list the html with id todo-list?
;; A: it "patches" by the id
(defn todo-list 
  "Building up the ul for todo items."
  [items]
  ;; Setting up for d*/patch-elements!
  ;; it will patch the element in the "home" by id, e.g.: <ul id="todo-list">...</ul>
  [:ul {:id "todo-list"}
   (for [item items]
     (list-item item))])

(todo-list (select-todos))


;; HTML
(def home-page
  "Ingesting html file"
  (slurp (io/resource "home.html")))

(defn home 
"Rendering HTML in string for the index page from Hiccup elements"
  [_] 
  (-> (html/home)
      hp/html
      str
      (resp/response)
      (resp/content-type "text/html")))

(defn str-html [el]
  (str
   (hp/html el)))

;; Ring Handlers
(defn gen-list 
 "Router handler for returning todo list" 
  [request]
  (let [items (select-todos)]
  ;; sse response and patch to HTML element by id
    (hk-gen/->sse-response
     request
     {hk-gen/on-open
      (fn [sse-gen]
        ;; Realising the patch by the element id, take a look inside the todo-list function.
        (d*/patch-elements! sse-gen (-> (todo-list items)
                                        hp/html
                                        str))
        (d*/close-sse! sse-gen))})))

(-> (todo-list (select-todos))
    hp/html
    str)

(defn item-done [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (mark-done! id)
    (gen-list request)))

(defn item-delete [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (delete-todo! id)
    (gen-list request)))

(defn insert-item [request]
  (let [params (:params request)
        title (get params "title")
        description (get params "description")]
    (insert-todo! title description)
    (gen-list request)))

;; Router
(def routes
  [["/" {:handler home}]
   ["/todos" {:get gen-list
              :post insert-item}]
   ["/todos/:id" {:patch item-done
                  :delete item-delete}]])
(def router (reitit.ring/router
             routes
             {:data {:middleware [parameters/parameters-middleware]}}))
(def handler (reitit.ring/ring-handler router))

;; Server
(defonce !server (atom nil))

(defn stop! []
  (when-not (nil? @!server)
    (@!server :timeout 1000)
    (reset! !server nil)))

(defn start! [handler opts]
  (stop!)
  (reset! !server
          (server/run-server
           handler
           (merge {:port 8000}
                  opts)))

  (println "Server running on " (get opts :port 8080)))

(defn start-sys
  "starting the server, then connect db and migrate"
  []
  (migrate-db!)
  (start! #'handler {:port 8080
                     :worker-pool (Executors/newVirtualThreadPerTaskExecutor)}))

(defn stop-sys
  "stopping the server then disconnect db"
  []
  (stop!)
  ;;(.close @db-pool)
  )

(defn restart-sys []
  (stop-sys)
  (start-sys))

(comment
  (stop!)
  (start-sys)
  (stop-sys)
 (restart-sys) 
  )

(defn -main
  [& _]
  (start-sys)
  (.addShutdownHook (Runtime/getRuntime)
                    (Thread. #(do (stop!) (shutdown-agents)))))