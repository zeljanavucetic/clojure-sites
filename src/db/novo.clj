(ns db.novo
  (:require prolefeed
            [clojure.xml :as xml]))




(defn fetch-clojure-reddit []
  (prolefeed/fetch "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss"))


(def zana (prolefeed/fetch "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss"))

(:title (first (:entries zana)))

(xml/parse "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss")

(get (:content (first (drop 9 (first (map :content (:content
(xml/parse "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss"))))))) 1)