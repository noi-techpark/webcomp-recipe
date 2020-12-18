import Vue from 'vue';
import App from './App.vue';
import VueI18n from 'vue-i18n';

import messagesEn from '@/assets/locales/en.json';
import messagesDe from '@/assets/locales/de.json';
import messagesIt from '@/assets/locales/it.json';

Vue.config.productionTip = false;
Vue.use(VueI18n);

const i18n = new VueI18n({
  locale: 'de',
  messages: {
    en: messagesEn,
    de: messagesDe,
    it: messagesIt,
  },
});

new Vue({
  i18n,
  render: (h) => h(App),
}).$mount('#app');
