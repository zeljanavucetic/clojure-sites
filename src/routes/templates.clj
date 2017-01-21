(ns routes.templates
  (:require [net.cgrand.enlive-html :as html]
            [rss.feed :as feed]
            [somnium.congomongo :as mongo]))

(defn create-link [site]
  (str "http://www." site))

(defn show-main [page sites]
  (html/at (html/html-resource page)
           [:table :tr.zana]
           (html/clone-for [site sites]
                           [:td :a]
                           (html/content (:Desclink site))
                           [:td :a]
                           (html/set-attr :href (create-link (:Desclink site)))
                           [:td.description]
                           (html/content (:Descriptionvalue site))
                           [:td.country]
                           (html/content (:Country site)))))


(defn show-country-page [sites country]
  (html/at (html/html-resource "byCountry.html")
           [:h1]
           (html/content country)
           [:table :tr.zana]
           (html/clone-for [site sites]
                           [:td :a]
                           (html/content (:Desclink site))
                           [:td :a]
                           (html/set-attr :href (create-link (:Desclink site)))
                           [:td.description]
                           (html/content (:Descriptionvalue site))
                           [:td.rank]
                           (html/content (:Rank site)))))

(defn show-rss [urls rss link req]
  (html/at (html/html-resource "rss.html")
           [:select.selectpicker]
           (html/set-attr :title req)
           [:select.selectpicker [:option html/first-of-type]]
           (html/clone-for [url urls] (html/content (:display_name url)))
           [:p :a]
           (html/content (:name link))
           [:p :a]
           (html/set-attr :href (:url link))
           [:ul.rss]
           (html/clone-for [r rss]
                           [:li.datum]
                           (html/html-content (:pubdate r))
                           [:li.vest]
                           (html/html-content (:description r)))))

(defn show-get-url-for-rss [languages]
  (html/at (html/html-resource "rssFeedURL.html")
           [:select.languages [:option html/first-of-type]]
           (html/clone-for [l languages]
                           [:option]
                           (html/content (:Headlinelink l))
                           [:option]
                           (html/set-attr :value (:Value l)))))

(defn show-add-new-site [countries]
  (html/at (html/html-resource "siteURL.html")
           [:select.countries [:option html/first-of-type]]
           (html/clone-for [c countries] (html/content c))))

(defn show-gelt-news [countries languages]
  (html/at (html/html-resource "gdelt.html")
           [:select.countries :option]
           (html/clone-for [c countries]
                           [:option]
                           (html/content (:Countrylink c))
                           [:option]
                           (html/set-attr :value (:Countrylink c)))
           [:select.lanSearch :option]
           (html/clone-for [l languages]
                           [:option]
                           (html/content (:Headlinelink l))
                           [:option]
                           (html/set-attr :value (:Headlinelink l)))
           [:select.lanNews :option]
           (html/clone-for [l languages]
                           [:option]
                           (html/content (:Headlinelink l))
                           [:option]
                           (html/set-attr :value (:Headlinelink l)))))
