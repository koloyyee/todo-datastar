#!/usr/bin/env bb
(require '[babashka.process :refer [shell]])

(def datomic-pro-version "1.0.7469")
(def datomic-home (str (System/getProperty "user.home") "/datomic/datomic-pro-" datomic-pro-version))
(def transactor (str datomic-home "/bin/transactor"))
(def properties-file (str (System/getProperty "user.dir") "/resources/db/config/sql-transactor.properties"))

(println transactor)
(println properties-file)
(try
  (println "Starting Datomic Transactor.")
  (shell transactor properties-file)
  (catch Exception e (println (.getMessage e))))
