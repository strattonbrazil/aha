(ns aha.db.schema
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.query :refer :all]))

(defn get-connection [] (cc/connect ["127.0.0.1"]))

(defn initialized?
  "checks to see if the database schema is present"
  []
  (let [conn (get-connection)
        keyspace "aha_keyspace"]
    (cql/create-keyspace conn keyspace
                         (if-not-exists)
                         (with {:replication
                                {:class "SimpleStrategy"
                                 :replication_factor 1}}))
    false)
)

(defn create-menus-table []
  (let [conn (get-connection)]
    (cql/use-keyspace conn "aha_keyspace")
    (cql/create-table conn "menus"
                      (if-not-exists)
                      (column-definitions {:id :timestamp
                                           :label :varchar
                                           :primary-key [:id]})))
  )

(defn create-pages-table []
  (let [conn (get-connection)]
    (cql/use-keyspace conn "aha_keyspace")
    (cql/create-table conn "pages"
                      (if-not-exists)
          (column-definitions {:id :timestamp
                               :label :varchar
                               :menu :timestamp
                               :primary-key [:id]})))
)

(defn create-tables
  "creates the database tables used by the application"
  []
  (println "initializing tables")
  (create-menus-table)
  (create-pages-table)
  (println "tables initialized")
)
