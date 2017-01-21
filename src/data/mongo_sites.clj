(ns data.mongo-sites
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
	
;parse number
(let [m (.getDeclaredMethod clojure.lang.LispReader
                            "matchNumber"
                            (into-array [String]))]
  (.setAccessible m true)
  (defn parse-number [s]
    (.invoke m clojure.lang.LispReader (into-array [s]))))

(defn provera [s]
  (if (= (parse-number s) nil)
    0
    (parse-number s)))

(defn get-sites []
  "get sites with some informations sites  collection"
  (mongo/fetch :sites :only  {
                              :_id false
                              :Desclink true
                              :Descriptionvalue true
                              :Rank true
                              :Country true}))

(defn site-exists? [site-name]
  "Check if site with certain name exists."
  (if (not= 0 (count
                (filter not-empty
                        (mongo/fetch :sites :only {:_id false} :where {:Desclink (if (or (.contains site-name "https://") (.contains site-name "http://"))
                                                                                   (if (or (.contains site-name "https://www.") (.contains site-name "http://www."))
                                                                                     (if (.contains site-name "https://www.")
                                                                                       (subs site-name 12)
                                                                                       (subs site-name 11))
                                                                                     (if (.contains site-name "https://")
                                                                                       (subs site-name 8)
                                                                                       (subs site-name 7)))
                                                                                   site-name)})))) true false))

(defn add-site [site-name description rank country]
  "If the site doesn't exist in the database, insert the site."
  (if-not (site-exists? site-name)
    (do
      (mongo/insert! :sites {:Desclink (if (or (.contains site-name "https://") (.contains site-name "http://"))
                                         (if (or (.contains site-name "https://www.") (.contains site-name "http://www."))
                                           (if (.contains site-name "https://www.")
                                             (subs site-name 12)
                                             (subs site-name 11))
                                           (if (.contains site-name "https://")
                                             (subs site-name 8)
                                             (subs site-name 7)))
                                         site-name)
                             :Descriptionvalue description
                             :Rank (if (> (provera rank) 5)
                                     "5"
                                     (str (provera rank)))
                             :Country country})
      (resp/response "Insertion succeeded!"))
    (resp/response "Site exists!")))



(defn delete-site [site-name]
  "Delete certain site."
  (do
    (mongo/destroy! :sites {:Desclink site-name})
    (resp/response "Successfully deleted!")))

(defn get-sites-only-for-specific-country [country]
  "Get sites for specified country."
  (mongo/fetch :sites :where
               {:Country country} :only {:_id false
                                         :Desclink true
                                         :Descriptionvalue true
                                         :Rank true}))

(defn get-distinct-values [key]
  "Get all values of some key - distinct."
 (sort-by str (distinct (flatten (map vals
                           (mongo/fetch
                             :sites :only
                             {:_id false
                              key true}))))))




(defn update-site [desclink description rank]
  "Update informations about site."
  (let [wrong-site (mongo/fetch-one :sites :where {:Desclink desclink})]
    (do
      (mongo/update! :sites wrong-site (merge wrong-site {:Desclink desclink
                                                          :Descriptionvalue description
                                                          :Rank (if (> (provera rank) 5)
                                                                  "5"
                                                                  (str (provera rank)))}))
      (resp/response "Site updated!"))))




(defn ja [desclink]
  (+ (provera
       (:Rank (mongo/fetch-one :sites :where
                               {:Desclink desclink}))) 1))

(defn increase-rank [desclink]
  (let [wrong-site (mongo/fetch-one :sites :where {:Desclink desclink})]
    (mongo/update! :sites wrong-site (merge wrong-site {:Desclink desclink
                                                        :Rank
                                                        (if (> (ja desclink) 5)
                                                          "5"
                                                          (str (ja desclink)))}))))




