(ns aha.layout
  (:require [selmer.parser :as parser]
            [clojure.string :as s]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]
            [environ.core :refer [env]]))

(def template-path "templates/")

(deftype RenderableTemplate [template params]
  Renderable
  (render [this request]
    (content-type
      (->> (assoc params
                  (keyword (s/replace template #".html" "-selected")) "active"
                  :dev (env :dev)
                  :servlet-context
                  (if-let [context (:servlet-context request)]
                    ;; If we're not inside a serlvet environment (for
                    ;; example when using mock requests), then
                    ;; .getContextPath might not exist
                    (try (.getContextPath context)
                         (catch IllegalArgumentException _ context))))
        (parser/render-file (str template-path template))
        response)
      "text/html; charset=utf-8")))

(defn render [template & [params]]
  (RenderableTemplate. template params))

(defn header-widget []
  {:type :header
   :text "Aha Header"}
)

(defn add-widget-ids [widgets]
  (let [ids (take (count widgets) (range))]
    (map (fn [widget id] (assoc widget :id (str "element-" id))) widgets ids))
)

(defn layout []
  (let [widgets [{:type :foo} {:type :bar}]]
    {:content-type :json 
     :body {:header "Hey, Person!" :widgets (add-widget-ids widgets)} }
    )
)
