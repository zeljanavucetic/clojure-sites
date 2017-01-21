(ns data.mongo-rss
  (:require
    [somnium.congomongo :as mongo]
    [ring.util.response :as resp]))


	
(def conn
  (mongo/make-connection "sites-clojure"
                         :host "127.0.0.17"
                         :port 27017))

;set the connection globally
(mongo/set-connection! conn)
(mongo/set-write-concern conn :strict)

(defn rssFeedURL-exists? [url]
  "Check if rss feed url exists."
  (if (not= 0 (count
                (filter not-empty
                        (mongo/fetch :rss :only {:_id false} :where {:url url})))) true false))



(defn add-new-rssFeedURL [link display-name name]
  "If the url doesn't exist in the collection :rss, insert the url."
  (if-not (rssFeedURL-exists? link)
    (do
      (mongo/insert! :rss {:url link
                           :display_name display-name
                           :name name})
      (resp/response "Creation succeeded!"))
    (resp/response "Rss feed url exists!")))


(defn get-rssFeedURL [name]
  "Get rss feed url from database with certain display_name-key"
  (mongo/fetch-one :rss :only {:_id false
                               :url true
                               :display_name true
                               :name true} :where {:display_name name}))
