(ns todo-datastar.html
  (:require
	[hiccup2.core :as hp]
	))

(defn header [body]
   [:html {:lang "en"}
    [:head
     [:meta {:charset "UTF-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
     [:title "Todo Datastar"]
    ;; [:link {:rel "stylesheet" :href "/css/styles.css"}]
   		[:script {:type "module" :src "https://cdn.jsdelivr.net/gh/starfederation/datastar@main/bundles/datastar.js"}]
  		 ]
 		 body])

(defn home []
   (header 
   [:body
    [:h1 "Datastar Todo List"]
    [:ul {:id "todo-list"}]
    [:button {:data-on-click "@get('/todos')"} "Get Todos"]
    [:form {:id "create-item-form"}
     [:label {:for "title"} "Title"]
     [:input {:type "text" :id "title" :name "title"}]
     [:label {:for "description"} "Description"]
     [:input {:type "text" :id "description" :name "description"}]
     [:button {:type "submit" :data-on-click "@post('/todos', {contentType: 'form'})"} "Add Item"]]]))

(->
 (home)
 (hp/html)
 str)

(defn create-item-form []
	[:form {:id "create-item-form"}
	 [:label {:for "title"} "Title:"]
	 [:input {:type "text" :id "title" :name "title"}]
	 [:label {:for "description"} "Description:"]
	 [:textarea {:id "description" :name "description"}]
	 [:button {:type "submit"} "Add Item"]])
