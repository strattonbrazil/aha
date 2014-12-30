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
  (db/save-page "my page" "some message")
  (println (db/get-pages))
  (layout/render "about.html" {:user (session/get :user)}))

(defn error-page [params]
  (layout/render 
   "error.html" {:content (params :message)}))

(defn login-page []
  (layout/render "login.html"))

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
)
