(ns aha.login
  (:require [clojure.string :as str])
  (:require [clj-http.client :as client])
  (:require [clojure.data.codec.base64 :as b64])
  (:require [clojure.data.json :as json])
  (:use ring.util.response))

(use '[ring.util.codec :only [url-encode]])

(defn make-query-string [m]
  (->> (for [[k v] m]
         (str (url-encode k) "=" (url-encode v)))
       (interpose "&")
       (apply str)))

(defn login [params] 
  (let [google-oauth2-client-id (System/getenv "AHA_GOOGLE_OAUTH2_CLIENT_ID")
        base-url "https://accounts.google.com/o/oauth2/auth"
        args {"client_id" google-oauth2-client-id
              "response_type" "code"
              "scope" "openid email"
              "redirect_uri" "http://localhost:3000/oauth2Callback"
              "state" "anti-forgery here"}]

    (assert google-oauth2-client-id "can't find AHA_GOOGLE_OAUTH2_CLIENT_ID in environment")

    (redirect (str base-url "?" (make-query-string args)))
    )
)

(defn request-token [code]
  (client/post "https://www.googleapis.com/oauth2/v3/token"
               {:basic-auth ["user" "pass"]
                :body "{\"json\": \"input\"}"
                :headers {"X-Api-Version" "2"}
                :content-type :application/x-www-form-urlencoded
                :form-params {:code code
                              :client_id (System/getenv "AHA_GOOGLE_OAUTH2_CLIENT_ID")
                              :client_secret (System/getenv "AHA_GOOGLE_OAUTH2_CLIENT_SECRET")
                              :redirect_uri "http://localhost:3000/oauth2Callback" ; ignored
                              :grant_type "authorization_code"
                              }
                :as :auto
                })
)

(defn string-to-base64-string [encoded]
  (String. (b64/decode (.getBytes encoded)) "UTF-8"))

(defn get-admin-users []
  (let [emails-str (System/getenv "AHA_ADMIN_EMAILS")] ; comma-delimited
    (assert emails-str "can't find AHA_ADMIN_EMAILS in environment")
    (str/split emails-str #",")
    )
)

(defn pad-payload [payload]
  
)

; parses out the payload from the token and adds necessary padding
(defn parse-jwt-token [jwt-token]
  (let [payload (second (str/split jwt-token #"\."))]
    (case (mod (.length payload) 4)
      0 payload
      2 (str payload "==")
      3 (str payload "=")))
)

; successfully provided oauth2 credentials
(defn login-success [params] 
  (let [resp (request-token (params :code))
        admin-users (get-admin-users)
        payload-encoded (parse-jwt-token ((resp :body) :id_token))
        payload-json (string-to-base64-string payload-encoded)        
        payload (json/read-str payload-json)
        email (payload "email")]
    (println admin-users)
    (if (false? (nil? (some #{email} admin-users))) ; log them in!      
      (redirect "/about") 
      (let [message (str "Unrecognized admin email: " email)]
        (redirect (str "/error?message=" (url-encode message))))
      )
    )
)

(defn login-failure [params]
  (redirect "http://www.yahoo.com"))

(defn oauth2-callback [params]
  (if (contains? params :error) 
    (login-failure params) ; permission denied
    (login-success params))
)

