<template>
  <div id="app">
    <item-detail
        v-if="detailContentId"
        :content-id="detailContentId"
        :is-list-available="islistAvailable"
        :content-type="contentType"
        @close="closeDetail"
    />
    <items-list v-else @show-detail="showDetail" :language="language" :contentType="contentType"
                :contentIdList="contentIdList" :pageSize="pageSize" :category="category"/>
  </div>
</template>

<script lang="ts">
import Vue from 'vue';
import ItemsList from './components/ItemsList.vue';
import ItemDetail from './components/ItemDetail.vue';
export default Vue.extend({
  components: {
    ItemsList,
    ItemDetail,
  },
  props: {
    contentType: {
      type: String,
      default: 'Activity'
    },
    contentIdList: {
      type: String,
      default: null,
    },
    category: {
      type: String,
      default: null
    },
    language: {
      type: String,
      default: 'de'
    },
    pageSize: {
      type: Number,
      default: 20
    }
  },
  data() {
    return {
      detailContentId: null,
    };
  },
  computed: {
    contentIds() {
      return this.contentIdList !== null ? this.contentIdList.split(',') : [];
    },
    islistAvailable() {
      return this.contentIds.length > 1;
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
    }
  },
});
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>