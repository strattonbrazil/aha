(ns aha.routes.home
  (:require [compojure.core :refer :all]
            [aha.layout :as layout]
            [aha.login :as login]
            [aha.util :as util]))

(defn home-page []
  (layout/render
    "home.html" {:content (util/md->html "/md/docs.md")}))

(defn about-page []
  (layout/render "about.html"))

(defn login-page []
  (layout/render "login.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/login" [] (login-page))
  (POST "/login" {params :params} (login/login params))
  (GET "/oauth2Callback" {params :params} (login/oauth2-callback params)))
