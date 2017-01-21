(ns rss.stat
  (:require [clojure.string :as string]))

(def keywords ["Clojure" "Groovy" "Python"])

(defn find-items [rss-item-list keyword]
  (filter #(.contains (first (:title %)) keyword) (flatten rss-item-list)))

(defn find-items-keywords [rss-item-list keyword-list]
  (flatten (map #(find-items rss-item-list %) keyword-list)))

(rss.stat/find-items-keywords
  (map :items (flatten (rss.data/parse-rss
                         (rss.feed/get-feed
                           "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss"))))
  ["GLAS" "Marko Đurić"])



(def netacnaVest (flatten (:description  (first (flatten (map :items (flatten (rss.data/parse-rss
                                                                                (rss.feed/get-feed
                                                                                  "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss")))))))))



(apply str (remove #{\\ \"} (apply str netacnaVest)))


;(def netacanLink (:link  (first (flatten (map :items (flatten (rss.data/parse-rss
;                                                      (rss.feed/get-feed
;         "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss"))))))))
;
;(def tacanLink (apply #(subs % (- (string/index-of f "=http://") 1)) (flatten netacanLink)))
;
;(def duzinaLinka (count tacanLink))
;
;(def brojPrvogLinka (string/index-of netacnaVest tacanLink))
;
;(def poluTacnaVest (apply #(subs % brojPrvogLinka) (flatten netacnaVest)))
;
;(def brojDrugogLinka (string/index-of poluTacnaVest tacanLink))
;
;(def poluTacnaVest2 (subs poluTacnaVest (+ brojDrugogLinka duzinaLinka 2)))
;
;(def br2 (string/index-of new (apply #(subs % (- (string/index-of f "=http://") 1)) (flatten f))))
;(def new (apply #(subs %  br) (flatten vest)))
;
;(apply #(subs %  br2) new)


