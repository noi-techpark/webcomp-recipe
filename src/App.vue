<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: AGPL-3.0-or-later
-->

<template>
  <div id="app">
    <item-detail
      v-if="detailContentId"
      :content-id="detailContentId"
      :is-list-available="islistAvailable"
      :content-type="contentType"
      :language="language"
      @close="closeDetail"
    />
    <items-list
      v-else
      @show-detail="showDetail"
      @change-current-page="changeCurrentPage"
      :language="language"
      :contentType="contentType"
      :contentIdList="contentIdList"
      :pageSize="pageSize"
      :category="category"
      :current-page="currentPage"
      :defaultPresentation="defaultPresentation"
    />
    <div id="footer">
        <a href="https://opendatahub.com" target="_blank"
          ><span id="footer-text">powered by Open Data Hub</span>
          <img
            :src="require('@/assets/icons/NOI_OPENDATAHUB_NEW_WH-01.png')"
            height="35px"
        /></a>
     </div>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import ItemsList from './components/ItemsList.vue';
import ItemDetail from './components/ItemDetail.vue';
import VueI18n from 'vue-i18n';

import messagesEn from '@/assets/locales/en.json';
import messagesDe from '@/assets/locales/de.json';
import messagesIt from '@/assets/locales/it.json';

Vue.use(VueI18n);

const i18n = new VueI18n({
  locale: 'de',
  messages: {
    en: messagesEn,
    de: messagesDe,
    it: messagesIt,
  },
});

export default Vue.extend({
  i18n,
  components: {
    ItemsList,
    ItemDetail,
  },
  props: {
    contentType: {
      type: String,
      default: 'Recipe',
    },
    contentIdList: {
      type: String,
      default: '',
    },
    category: {
      type: String,
      default: null,
    },
    language: {
      type: String,
      default: 'de',
    },
    pageSize: {
      type: Number,
      default: 20,
    },
    defaultPresentation: {
      type: String,
      default: 'grid',
    },
  },
  data() {
    return {
      detailContentId: null,
      currentPage: 1,
    };
  },
  computed: {
    contentIds() {
      return this.contentIdList !== null
        ? this.contentIdList.split(',').filter((e) => e)
        : [];
    },
    islistAvailable() {
      return this.contentIds.length != 1;
    },
  },
  watch: {
    language: {
      immediate: true,
      handler(value) {
        this.$i18n.locale = value;
      },
    },
  },
  created() {
    if (!this.islistAvailable) {
      this.detailContentId = this.contentIds[0];
    }
  },
  methods: {
    showDetail(detailContentId) {
      this.detailContentId = detailContentId;
    },
    closeDetail() {
      this.detailContentId = null;
    },
    changeCurrentPage(pageNum) {
      this.currentPage = pageNum;
    },
  },
});
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/**********************
FOOTER
**********************/

#footer {
  margin-top: 25px;
}

#footer > a {
  display: flex;
  color: darkgrey;
  text-decoration: none;
  z-index: 100001;
  justify-content: flex-end;
  align-items: center;
}

#footer-text {
  color: darkgrey;
  margin-right: 10px;
}
</style>
