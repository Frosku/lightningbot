(ns lightningbot.booru.booru
  (:require [clj-http.client :as cc]))

(defn api-root
  [cfg]
  (str (get-in cfg [:philomena :web-root]) "api/v1/json/"))

(defn api-search-images-endpoint
  [cfg]
  (str (api-root cfg) "search/images"))

(defn api-image-endpoint
  [cfg]
  (str (api-root cfg) "images/"))

(defn api-filters-endpoint
  [cfg]
  (str (api-root cfg) "filters/"))

(defn get-image-by-id
  [cfg filter image-id]
  (-> cfg
      (api-image-endpoint)
      (str image-id)
      (cc/get {:accept :json
               :as :json
               :query-params {"key" (get-in cfg [:philomena :api-key])
                              "filter_id" filter}})
      (get-in [:body :image])))

(defn get-number-of-results
  [cfg filter query]
  (-> cfg
      (api-search-images-endpoint)
      (cc/get {:accept :json
               :as :json
               :query-params {"key" (get-in cfg [:philomena :api-key])
                              "filter_id" filter
                              "q" query
                              "per_page" 1}})
      (get-in [:body :total])))

(defn get-search-image-results
  [cfg filter query per-page page-num]
  (-> cfg
      (api-search-images-endpoint)
      (cc/get {:accept :json
               :as :json
               :query-params {"key" (get-in cfg [:philomena :api-key])
                              "filter_id" filter
                              "q" query
                              "per_page" per-page
                              "page" page-num}})
      (get-in [:body :images])))

(defn public-filter-exists?
  [cfg filter-id]
    (-> cfg
        (api-filters-endpoint)
        (str filter-id)
        (cc/get {:accept :json})
        (get-in [:status])
        (= 200)))