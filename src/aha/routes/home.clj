(ns aha.routes.home
  (:require [compojure.core :refer :all]
            [noir.session :as session]
            [aha.layout :as layout]
            [aha.login :as login]
            [aha.db.core :as db]
            [aha.util :as util])
  (:use ring.util.response))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn admin-page []
  (let [user (session/get :user)]
    (if (nil? user) (redirect "/login")
        (layout/render "admin.html" {:user (session/get :user)}))))

(defn about-page []
  (db/save-page "new page" "gallery")
  (layout/render "about.html" {:user (session/get :user)}))

(defn error-page [params]
  (layout/render 
   "error.html" {:content (params :message)}))

(defn login-page []
  (layout/render "login.html"))

(defn get-pages []
  (let [pages (db/get-pages)
        menus (db/get-menus)]
    {:content-type :json
     :body { :pages pages :menus menus }
     }))

(defn get-menus []
  {:content-type :json :body { :menus (db/get-menus) } }
)


(defn save-menu [params]
  (db/save-menu (params :label))

  { :content-type :json :body {} }
)

(defn save-page [params]
  (db/save-page (params :title) "gallery" (read-string (params :menuId)))

  { :content-type :json :body {} }
)

(defn move-page [params]
  (db/move-page (read-string (params :pageId))
                (read-string (params :srcMenuId))
                (read-string (params :srcIndex))
                (read-string (params :targetMenuId))
                (read-string (params :targetIndex)))

  { :content-type :json :body {} }
)

(defn update-menu [params]
  (db/update-menu (params :id) (dissoc params :id))

  { :content-type :json :body {} }
)

(defn remove-menu [id]
  (db/remove-menu id)

  { :content-type :json :body {} }
)

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/admin" [] (admin-page))
  (GET "/about" [] (about-page))
  (GET "/error" {params :params} (error-page params))
  (GET "/login" [] (login-page))
  (POST "/login" {params :params} (login/login params))
  (GET "/oauth2Callback" {params :params} (login/oauth2-callback params))
  (GET "/logout" [] (login/logout))
  (GET "/layout" [] (layout/layout))

  ; for admin stuff
  (GET "/pages" [] (get-pages))
  (POST "/pages" { params :params } (save-page params))
  (PATCH "/pages/:pageId/order" { params :params } (move-page params))
  (GET "/menus" [] (get-menus))
  (POST "/menus" { params :params } (save-menu params))
  (PATCH "/menus/:id" { params :params } (update-menu params))
  (DELETE "/menus/:id" [id] (remove-menu id))
)
