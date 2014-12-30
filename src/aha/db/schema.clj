(ns aha.db.schema
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.io :refer [file]]
            [noir.io :as io]))

;; TODO: make this a system env
(def db-store (str (.getName (file ".")) "/aha"))

(def db-spec {:classname "org.h2.Driver"
              :subprotocol "h2"
              :subname db-store
              :user "sa"
              :password ""
              :make-pool? true
              :naming {:keys clojure.string/lower-case
                       :fields clojure.string/upper-case}})

(defn initialized?
  "checks to see if the database schema is present"
  []
  (.exists (new java.io.File (str db-store ".h2.db"))))


(defn create-pages-table []
  (sql/db-do-commands
    db-spec
    (sql/create-table-ddl
      :aha_pages
      [:id "INTEGER PRIMARY KEY AUTO_INCREMENT"]
      [:name "varchar(100)"]
      [:message "varchar(100)"]
      [:created :timestamp]))
  (sql/db-do-prepared db-spec
      "CREATE INDEX timestamp_index ON aha_pages (created)"))

(defn create-tables
  "creates the database tables used by the application"
  []
  (create-pages-table))
