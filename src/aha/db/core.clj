(ns aha.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [aha.db.schema :as schema]))

(defdb db schema/db-spec)

(defentity aha_pages)

(defn save-page [name message]
  (insert aha_pages
          (values {:name name
                   :message message
                   :created (new java.util.Date)})))

(defn get-pages []
  (select aha_pages))
