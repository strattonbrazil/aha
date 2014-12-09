(ns aha.login
  (:require [clj-http.client :as client])
  (:use ring.util.response))

(use '[ring.util.codec :only [url-encode]])

(defn make-query-string [m]
  (->> (for [[k v] m]
         (str (url-encode k) "=" (url-encode v)))
       (interpose "&")
       (apply str)))

(defn login [params] 
  (let [google-oauth2-client-id (System/getenv "GOOGLE_OAUTH2_CLIENT_ID")
        base-url "https://accounts.google.com/o/oauth2/auth"
        args {"client_id" google-oauth2-client-id
              "response_type" "code"
              "scope" "openid email"
              "redirect_uri" "http://localhost:3000/oauth2Callback"
              "state" "anti-forgery here"}]

    (assert google-oauth2-client-id "can't find GOOGLE_OAUTH2_CLIENT_ID in environment")

    (redirect (str base-url "?" (make-query-string args)))
    )
)

(defn oauth2-callback [params]
  (println params)

  (let [resp
  (client/post "https://www.googleapis.com/oauth2/v3/token"
               {:basic-auth ["user" "pass"]
                :body "{\"json\": \"input\"}"
                :headers {"X-Api-Version" "2"}
                :content-type :application/x-www-form-urlencoded
                :form-params {:code (params :code)
                              :client_id (System/getenv "GOOGLE_OAUTH2_CLIENT_ID")
                              :client_secret (System/getenv "GOOGLE_OAUTH2_CLIENT_SECRET")
                              :redirect_uri "http://localhost:3000/oauth2Callback" ; ignored
                              :grant_type "authorization_code"
                              }
                :as :auto
                })
        ]
    (println resp)
    (println (resp :body))
    (println (type (resp :body)))
    (println ((resp :body) :id_token))
    (println (decode ((resp :body) :id_token)))
    )

;https://developers.google.com/wallet/digital/docs/jwtdecoder

  (redirect "/about")
)

;{:session_state 6bb0c9edb2cd5781090d3e81d791bade77da246f..44e1, :prompt consent, :num_sessions 2, :authuser 0, :code 4/meWw5MOdzVzC_3Da0WnAf3l5Q9Fq6UNmez5-_UxZmd8.8gn_izwXKcQcEnp6UAPFm0GOI6pFlAI, :state anti-forgery here}
