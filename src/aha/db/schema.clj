(ns aha.db.schema
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :refer [file]]
            [noir.io :as io]))

(def db-store (str (.getName (file ".")) "/aha.db"))

(def db-spec {:classname "org.sqlite.JDBC"
              :subprotocol "sqlite"
              :subname db-store
              :user "sa"
              :password ""
              :make-pool? true
              :naming {:keys clojure.string/lower-case
                       :fields clojure.string/upper-case}})

(defn initialized?
  "checks to see if the database file is present"
  []
  (.exists (new java.io.File db-store))
)

(defn create-menus-table []
  (sql/db-do-commands
   db-spec
   (sql/create-table-ddl
    :menus
    [:id "INTEGER PRIMARY KEY"]
    [:label "varchar(100)"]))
  )

(defn create-pages-table []
  (sql/db-do-commands
   db-spec
   (sql/create-table-ddl
    :pages
    [:id "INTEGER PRIMARY KEY"]
    [:label "varchar(100)"]
    [:type "varchar(100)"]

    ; foreign keys are not enabled by default
    [:parent :serial "references pages (id)"]
    [:menu :serial "references menus (id)"])) 
)

(defn create-tables
  "creates the database tables used by the application"
  []
  (println "initializing tables")
  (create-menus-table)
  (create-pages-table)
  (println "tables initialized")
)
