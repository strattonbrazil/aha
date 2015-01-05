(ns aha.db.core
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [aha.db.schema :as schema]))

(defdb db schema/db-spec)

(defentity menus)
(defentity pages)

(defn save-menu [label]
  (insert menus (values { :label label })))

(defn update-menu [id params]
  (update menus (set-fields params) (where { :id id }))
)

(defn remove-menu [id]
  (delete menus (where { :id id }))
)

(defn save-page [label type]
  (insert pages
          (values {:label label
                   :type type
                   :menu 3 })))

(defn get-menus [] (select menus))
(defn get-splash-page [] { :label "Splash Screen" :type :splash })
(defn get-pages [] 
  (cons (get-splash-page) (select pages))
)

