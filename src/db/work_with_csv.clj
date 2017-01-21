(ns db.work-with-csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))


(defn drop-last-4 [s]
  "For getting country-name from file-name"
  (apply str (drop-last 4 s)))

;pravljenje keyword-a na osnovu naziva drzave
;(defn country-keywordise [country-csv]
;  (keyword (drop-last-4 country-csv)))

(defn read-csv [data]
  "Read csv file."
  (with-open [reader (io/reader data)]
                          (doall (csv/read-csv reader))))

(defn str->keyword [s]
  "Make key from some string."
  (keyword (apply str (remove #{\space \﻿ \"} s))))

; header - first row in csv file
(defn keywordise [header]
  "Make sequence of keys based on sequence of strings)."
  (map str->keyword header))

; header - first row in csv file, rows - rest rows in csv file
(defn to-map [header rows]
  "Make map - every row has certain key."
  (map #(apply hash-map (interleave header %)) rows))

(defn read-data [file-name]
  "Read data in specific way to make right map of data."
  (to-map (keywordise (first (read-csv file-name))) (rest (read-csv file-name))))

(defn add-new-key [key content data]
  "Add new key with some content into map."
  (map #(assoc % key content) data))

;For my case
(defn read-certain-data [file-name]
  (add-new-key :Country (drop-last-4 file-name)
               (add-new-key :Rank ""
                            (read-data file-name))))

(defn all-csv-to-one-map [& args]
  "Concat several csv-files into one map."
  (apply concat (map read-certain-data args)))


(defn get-sequence-of-cetain-data-from-sequence-of-all-data [content data key]
  "Get certain data from all data where key has certain content."
  (filter #(= (key %) content) data))




