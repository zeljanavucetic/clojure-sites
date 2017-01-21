(ns routes.handler
  (:use (compojure handler
                   [core :only (GET POST ANY defroutes) :as compojure]))
  (:require
    [ring.adapter.jetty :only [run-jetty]]
    [net.cgrand.enlive-html :as html]
    [compojure.handler :as handler]
    [compojure.route :as route]
    [ring.middleware.session :as session]
    [routes.templates :as templates]
    [data.mongo-sites :as sites]
    [rss.feed :as feed]
    [rss.data :as data]
    [db.work-with-csv :as csv]
    [data.mongo-rss :as rss]
    [clojure.string :as string]
    [somnium.congomongo :as mongo]))




(defn get-parametar [request param]
  "Get certain parametar from post request"
  (get (:params request) param))

(defn make-url-for-rss [country lan word]
  (str "https://news.google.de/news/feeds?pz=1&cf=all&ned=" (string/trim country) "&hl=" (string/trim lan) "&q="
       (string/trim word) "&output=rss"))

;
;(def rss (feed/add-new-key-to-maps :day
;                              (feed/add-new-key-to-maps :month
;                                                   (feed/add-new-key-to-maps :year
;                                                                        (feed/add-new-key-to-maps :pubdate
;                                                                                             (feed/to-list-of-feed-maps mapa) (feed/to-list-of-pubdates datum))
;                                                                        (feed/to-list-parts-of-date :year datum feed/get-year-from-date))
;                                                   (feed/to-list-parts-of-date :month datum feed/get-month-from-date))
;                              (feed/to-list-parts-of-date :day datum feed/get-day-from-date)))
;
;
;(map :day (feed/to-list-parts-of-date :day datum feed/get-day-from-date))
;(def zana (reverse (group-by :year (reverse (sort-by :day (feed/add-new-key-to-maps :day
;                                                                                    (feed/add-new-key-to-maps :month
;                                                                                                              (feed/add-new-key-to-maps :year
;                                                                                                                                        (feed/add-new-key-to-maps :pubdate
;                                                                                                                                                                  (feed/to-list-of-feed-maps  (map :description  (flatten (map :items (flatten (data/parse-rss
;                                                                                                                                                                                                                                                 (feed/get-feed
;                                                                                                                                                                                                                                                   "https://news.google.de/news/feeds?pz=1&cf=all&q=Tomislav+Nikolic&output=rss"))))))) (feed/to-list-of-pubdates datum))
;                                                                                                                                        (feed/to-list-parts-of-date :year datum feed/get-year-from-date))
;                                                                                                              (feed/to-list-parts-of-date :month datum feed/get-month-from-date))
;                                                                                    (feed/to-list-parts-of-date :day datum feed/get-day-from-date)))))))


; prosledi f-ji reverse (sort-by :day (sort-by :month (reverse (sort-by :year rss))))

(defroutes app-routes
           (GET "/" [] (html/emit* (templates/show-main "main.html" (into []
                                                                          (take 10 (reverse (sort-by :Rank (sites/get-sites))))))))

           (GET "/slovenija" [] (html/emit* (templates/show-country-page (into []
                                                                               (sites/get-sites-only-for-specific-country "slovenia")) "slovenia")))

           (GET "/Albanija" [] (html/emit* (templates/show-country-page (into []
                                                                              (sort-by :Desclink (sites/get-sites-only-for-specific-country "Albanija"))) "Albanija")))

           (GET "/Balkan-Evropa" [] (html/emit* (templates/show-country-page (into []
                                                                                   (sort-by :Desclink (sites/get-sites-only-for-specific-country "Balkan-Evropa"))) "Balkan-Evropa")))

           (GET "/BiH" [] (html/emit* (templates/show-country-page (into []
                                                                         (sort-by :Desclink (sites/get-sites-only-for-specific-country "BiH"))) "BiH")))

           (GET "/Bugarska" [] (html/emit* (templates/show-country-page (into []
                                                                              (sort-by :Desclink (sites/get-sites-only-for-specific-country "Bugarska"))) "Bugarska")))

           (GET "/CrnaGora" [] (html/emit* (templates/show-country-page (into []
                                                                              (sort-by :Desclink (sites/get-sites-only-for-specific-country "Crna Gora"))) "Crna Gora")))

           (GET "/Hrvatska" [] (html/emit* (templates/show-country-page (into []
                                                                              (sort-by :Desclink (sites/get-sites-only-for-specific-country "Hrvatska"))) "Hrvatska")))

           (GET "/Madjarska" [] (html/emit* (templates/show-country-page (into []
                                                                               (sort-by :Desclink (sites/get-sites-only-for-specific-country "Madjarska"))) "Madjarska")))

           (GET "/Makedonija" [] (html/emit* (templates/show-country-page (into []
                                                                                (sort-by :Desclink (sites/get-sites-only-for-specific-country "Makedonija"))) "Makedonija")))

           (GET "/Mirovneoperacije" [] (html/emit* (templates/show-country-page (into []
                                                                                      (sort-by :Desclink (sites/get-sites-only-for-specific-country "Mirovne operacije"))) "Mirovne operacije")))

           

           (GET "/Ukrajina" [] (html/emit* (templates/show-country-page (into []
                                                                              (sort-by :Desclink (sites/get-sites-only-for-specific-country "Ukrajina"))) "Ukrajina")))


           (GET "/Ostalo" [] (html/emit* (templates/show-country-page (into []
                                                                            (sort-by :Desclink (sites/get-sites-only-for-specific-country "Ostalo"))) "Ostalo")))

           (GET "/Rumunija" [] (html/emit* (templates/show-country-page (into []
                                                                              (sort-by :Desclink (sites/get-sites-only-for-specific-country "Rumunija"))) "Rumunija")))

           (GET "/Bliskiistok" [] (html/emit* (templates/show-country-page (into []
                                                                                 (sort-by :Desclink (sites/get-sites-only-for-specific-country "Bliski istok"))) "Bliski istok")))

           (GET "/rss:req" [req] (html/emit* (templates/show-rss
                                               (into [] (mongo/fetch :rss :only {:_id false
                                                                                 :display_name true
                                                                                 :name true}))
                                               (feed/my-rss-map req)
                                               (rss/get-rssFeedURL req) req)))

           (GET "/addRss" [] (html/emit* (templates/show-get-url-for-rss
                                           (into []
                                                 (mongo/fetch :languages :only {:_id false
                                                                                :Value true
                                                                                :Headlinelink true})))))

           (GET "/addUrl" [] (html/emit* (templates/show-add-new-site
                                           (sites/get-distinct-values :Country))))

           (GET "/gdelt" [] (html/emit* (templates/show-gelt-news
                                          (into []
                                                (mongo/fetch :countries :only {:_id false
                                                                               :Countrylink true}))
                                          (into []
                                                (mongo/fetch :languages :only {:_id false
                                                                               :Headlinelink true})))))

           (POST "/deleteSite" request
             ;(def provera (:params request))
             (let [link (get (:params request) :link)]
               (sites/delete-site link)))

           (POST "/increaseRank" request
             (let [link (get (:params request) :link)] (sites/increase-rank link))
             ;(def provera (:params request))
             )

           (POST "/updateSite" request
             (let [desclink (get (:params request) :desclink)
                   description (get (:params request) :description)
                   rank (get (:params request) :rank)]
               (sites/update-site desclink description rank)))

           (POST "/addRss" request
             (rss/add-new-rssFeedURL
               (make-url-for-rss (get-parametar request :country)
                                 (get-parametar request :language)
                                 (get-parametar request :word))
               (str (get-parametar request :word) " - lang: " (get-parametar request :language)) (str (get-parametar request :word))))

           (POST "/addUrl" request
             (let [desclink (get (:params request) :desclink)
                   description (get (:params request) :description)
                   rank (get (:params request) :rank)
                   country (get (:params request) :country)]
               (sites/add-site desclink description rank country)))

           (POST "/zana" request
             (def f (:form-params request)))


           (route/resources "/")
           (route/not-found "<h1>Page not found</h1>"))





(def app
  (-> (handler/site app-routes)
      (session/wrap-session)))

(defn start-server []
  (ring.adapter.jetty/run-jetty #'app {:port 9000 :join? false}))


