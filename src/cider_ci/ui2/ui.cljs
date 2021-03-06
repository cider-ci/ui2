(ns cider-ci.ui2.ui
  (:require-macros
    [reagent.ratom :as ratom :refer [reaction]]
    )
  (:require
    [cider-ci.ui2.constants :refer [CONTEXT]]
    [cider-ci.ui2.ui.debug :as debug]
    [cider-ci.ui2.ui.root]
    [cider-ci.ui2.ui.commits]
    [cider-ci.ui2.ui.state :as state]
    [cider-ci.ui2.create-admin.ui]
    [cider-ci.ui2.welcome-page.ui]
    [cider-ci.ui2.session.password.ui]

    [cider-ci.ui2.ui.projects]
    [cider-ci.ui2.ui.navbar]

    [reagent.core :as reagent]
    [secretary.core :as secretary :include-macros true]
    [accountant.core :as accountant]

    [cljsjs.jquery]
    [cljsjs.bootstrap]
    ))

(def user (reaction (:user @state/server-state)))

(def authentication-providers
  (reaction (:authentication_providers @state/server-state)))

(def current-url (reaction (:current-url @state/client-state)))


(defn current-page []
  (let [location-href (-> js/window .-location .-href)
        location-url (goog.Uri. location-href)]
    (swap! state/client-state assoc
           :current-url location-href
           :current-path (.getPath location-url))
    [:div.container-fluid
     ;[:h1 "TEST"]
     [:div.page [(-> @state/page-state :current-page :component)]]
     [debug/general-debug-section]]))

;--- Routes


;--- Initialize

(defn mount []
  (when-let [app (.getElementById js/document "app")]
    (reagent/render [current-page] app))
  (when-let [nav-container (.getElementById js/document "nav")]
    (reagent/render [cider-ci.ui2.ui.navbar/navbar
                     user current-url authentication-providers] nav-container))
  )

(defn init! []
  (when-let [app (.getElementById js/document "app")]
    (accountant/configure-navigation!
      {:nav-handler (fn [path] (secretary/dispatch! path))
       :path-exists?  (fn [path] (secretary/locate-route path))})
    (accountant/dispatch-current!)
    (mount)))
