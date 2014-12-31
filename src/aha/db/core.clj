(ns aha.db.core
  (:require [aha.db.schema                 :as schema]
            [clojurewerkz.cassaforte.cql   :as cql]
            [clojurewerkz.cassaforte.uuids :as uuids]))

(defn save-page [name message]
  (let [conn (schema/get-connection)]
    (cql/use-keyspace conn "aha_keyspace")
    (cql/insert conn "pages" {:id (uuids/unix-timestamp (uuids/time-based))
                              :label "some page" 
                              :menu (uuids/unix-timestamp (uuids/time-based)) }))
)

(defn get-pages [] 
  (let [conn (schema/get-connection)]
    (cql/use-keyspace conn "aha_keyspace")
    (cql/select conn "pages"))
)
