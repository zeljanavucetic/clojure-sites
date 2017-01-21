(ns rss.feed
  (:require
    [clj-http.client :as client]
    [rss.data :as data]
    [clojure.string :as string]
    [data.mongo-rss :as rss]))

(defn get-feed [url]
  "Get feed from one RSS feed url"
  (:body (client/get url)))

(defn get-feeds [urllist]
  "Get feed from list of RSS feed url"
  (pmap get-feed urllist))


(defn makeRightHTML [wrongHTML]
  "Make right HTML code from received data"
  (apply str (remove #{\\ \"} (apply str wrongHTML))))

(defn add-new-key-to-map [key in-map out-map]
  "Add new key ton in-map with content from out-map"
  (assoc in-map key (key out-map)))

(defn to-map-with-the-key [key content]
  (hash-map key content))

(defn get-only-pubdate-and-description-from-received-data [source]
  "Extract content with certain keys from received map of data."
  (map (juxt :pubdate :description) (flatten (map :items (flatten (data/parse-rss
                                                                    (get-feed
                                                                      source)))))))

(defn to-my-feed-map [feed]
  "Make the right one map - assign to keys (:description, :pubdate) appropriate content (description, publish date)."
  (add-new-key-to-map :description (apply hash-map :pubdate (flatten (first feed)))
                      (hash-map :description (makeRightHTML (flatten (rest feed))))))

;finish
(defn my-rss-map [source]
  "Finish. Get appropriate sequence."
  (if (= source "none")
    nil
    (map to-my-feed-map (get-only-pubdate-and-description-from-received-data
                          (:url (rss/get-rssFeedURL source))))))

;parse number
(let [m (.getDeclaredMethod clojure.lang.LispReader
                            "matchNumber"
                            (into-array [String]))]
  (.setAccessible m true)
  (defn parse-number [s]
    (.invoke m clojure.lang.LispReader (into-array [s]))))


;recognize month
(defn month [value]
  (cond
    (= value "Jan") "1"
    (= value "Feb") "2"
    (= value "Mar") "4"
    (= value "Apr") "4"
    (= value "May") "5"
    (= value "Jun") "6"
    (= value "Jul") "7"
    (= value "Aug") "8"
    (= value "Sep") "9"
    (= value "Oct") "10"
    (= value "Nov") "11"
    (= value "Dec") "12"
    :else "nill"))

(defn get-day-from-date [date]
  (if (= (get (string/split date #"\s+") 1) "09")
    9
    (parse-number (get (string/split date #"\s+") 1))))

(defn get-month-from-date [date]
  (parse-number (month (get (string/split date #"\s+") 2))))

(defn get-year-from-date [date]
  (parse-number (get (string/split date #"\s+") 3)))






;VEZBA


;(def mapa (map :description (flatten (map :items (flatten (data/parse-rss
;                                                            (get-feed
;                                                              "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss")))))))
;
;(def datum (map :pubdate (flatten (map :items (flatten (data/parse-rss
;                                                         (get-feed
;                                                           "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss")))))))
;
;(defn to-list-of-feed-maps [feeds] (map to-map-feed feeds))
;
;(defn to-list-of-pubdates [pubdates]
;  (map #(to-map-with-the-key :pubdate %) (flatten pubdates)))
;
;(defn to-list-parts-of-date [key pubdates function]
;  (map #(to-map-with-the-key key %) (map function (map :pubdate (to-list-of-pubdates pubdates)))))


;(map #(to-map-with-the-key :year %) (map get-year-from-date (map :pubdate (to-list-of-pubdates datum))))
;
;(def rss (add-new-key-to-maps :day
;                              (add-new-key-to-maps :month
;                                                   (add-new-key-to-maps :year
;                                                                        (add-new-key-to-maps :pubdate
;                                                                                             (to-list-of-feed-maps mapala) (to-list-of-pubdates datum))
;                                                                        (to-list-parts-of-date :year datum get-year-from-date))
;                                                   (to-list-parts-of-date :month datum get-month-from-date))
;                              (to-list-parts-of-date :day datum get-day-from-date)))
;
;(def mapa (reverse (group-by :year (reverse (sort-by :day rss)))))


