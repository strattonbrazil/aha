(ns aha.db.core
  (:require [cheshire.core :refer :all])
  (:require [aha.db.schema :as schema]))

(defn save-menu [label] )

(defn update-menu [id params] )

(defn remove-menu [id] )

(defn get-db-data [] (parse-stream (clojure.java.io/reader schema/db-store)))

(defn save-db-data [data]
  (generate-stream data (clojure.java.io/writer schema/db-store)) {:pretty true})

; returns a menu with the page added
(defn add-page-to-menu [menu page menu-id position] 
  (if (= (menu "id") menu-id)
    (let [pages (into [] (menu "pages"))
          pages-before (subvec pages 0 position)
          pages-after (subvec pages position)]
      (println pages)
      (println (type pages))
      (println position)
      (println pages-before)
      (println pages-after)
      (assoc menu "pages" (concat (conj pages-before page) pages-after)))      
    menu)
)
; given a sequence of hashmaps return an unused integer id
(defn get-unique-id [collection]
  (let [current-ids (set (map (fn [element] (element "id")) collection))]
    (first (filter (fn [id] (not (contains? current-ids id))) (range))))
)

(defn get-menu-pages [menus]
  (flatten (map (fn [menu] (menu "pages")) menus))
)

(defn get-page [menus page-id]
  (let [pages (get-menu-pages menus)]
    (first (filter (fn [page] (= page-id (page "id"))) pages)))
)

; returns new menus with the page added
(defn add-page-to-menus [menus page menu-id]
  (let [page-id (get-unique-id (get-menu-pages menus))
        page (assoc page "id" page-id)
        num-pages-in-menu (fn [menu] (count (menu "pages")))]
    (map (fn [menu] (add-page-to-menu menu page menu-id (num-pages-in-menu menu))) menus))
)

(defn save-page [title type menu-id]
  (let [data (get-db-data)
        page { :title title :type type }]
    (save-db-data (update-in data [ "menus" ] (fn [menus] (add-page-to-menus menus page menu-id)))))
)

;(defn remove-page-from-menu [menu page-id]
;  (assoc menu "pages" (filter (fn [page] (= (page "id") page-id)) (menu "pages")))
;)

; returns the menus with the page removed
(defn remove-page-from-menus [menus page-id]
  (let [remove-page-from-menu (fn [menu page-id] (assoc menu "pages" (filter (fn [page] (not= (page "id") page-id)) (menu "pages"))))]
    (map (fn [menu] (remove-page-from-menu menu page-id)) menus))
)

; move a page from one menu to another or between menus
(defn move-page [page-id src-menu-id src-index target-menu-id target-index]
  (let [data (get-db-data)
        menus-without-page (remove-page-from-menus (data "menus") page-id)
        page (get-page (data "menus") page-id)
        menus-with-moved-page (map (fn [menu] (add-page-to-menu menu page target-menu-id target-index)) menus-without-page)]
    (save-db-data (assoc data "menus" menus-with-moved-page)))
)

(defn get-menus [] 
  (let [data (get-db-data)]
    (data "menus"))
)

(defn get-splash-page [] { :label "Splash Screen" :type :splash })
(defn get-pages [] 
;  (cons (get-splash-page) (select pages))
)

