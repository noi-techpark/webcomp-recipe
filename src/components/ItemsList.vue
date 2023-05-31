<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: AGPL-3.0-or-later
-->

<template>
  <div class="list">
    <div class="header">
      <h2 class="page-title">{{ $t('recipes') }}</h2>
      <div class="list-grid-switch">
        <div class="list-switch-button" @click="setShowList(true)">
          <ListBlueIcon v-if="showList"></ListBlueIcon>
          <ListIcon v-else></ListIcon>
        </div>
        <div class="grid-switch-button" @click="setShowList(false)">
          <GridIcon v-if="showList" class="grid-icon"></GridIcon>
          <GridBlueIcon v-else class="grid-icon-blue"></GridBlueIcon>
        </div>
      </div>
    </div>
    <div class="search-bar">
      <input
        type="text"
        class="search-input"
        :placeholder="$t('searchRecipe')"
        v-model="searchInput"
        @keyup="searchRecipeList"
      />
      <div class="search-button">
        <search-icon
          @click="loadRecipeList(currentPage)"
          width="24px"
          height="24pxs"
        ></search-icon>
      </div>
    </div>
    <paging
      :current-page="currentPage"
      :total-pages="totalPages"
      @next-page="nextPage"
      @last-page="lastPage"
      @go-to-page="goToPage"
    ></paging>
    <template v-if="items.length > 0 && showList">
      <div
        v-for="item of items"
        :key="item.id"
        @click.prevent="showDetail(item.Id)"
        class="item-container"
      >
        <hr class="solid" />
        <div class="list-item">
          <div
            v-if="item.ImageGallery === null || item.ImageGallery.length === 0"
          >
            <div
              style="min-height: 70px; min-width: 70px; max-height: 70px; max-width: 70px"
            >
              <RezeptIcon
                class="rezept-icon"
                viewBox="0 0 595.3 367.54"
                width="auto"
                height="70px"
                preserveAspectRatio="xMidYMid slice"
              ></RezeptIcon>
            </div>
          </div>
          <div v-else>
            <img class="thumbnail" :src="item.ImageGallery[0].ImageUrl" />
          </div>
          <div class="info">
            <div class="title">{{ getTitle(item, language) }}</div>
            <div class="short-info">{{ getRecipeShortInfo(item) }}</div>
          </div>
          <div class="arrow-icon">
            <arrow-icon-right viewBox="0 0 24 24" width="100%" height="100%" />
          </div>
        </div>
      </div>
    </template>
    <template v-else-if="items.length > 0 && !showList">
      <div
        v-for="item of items"
        :key="item.id"
        @click.prevent="showDetail(item.Id)"
        class="grid-container"
      >
        <div class="info-grid">
          <div
            v-if="item.ImageGallery === null || item.ImageGallery.length === 0"
          >
            <div style="">
              <RezeptIcon
                class="rezept-icon"
                viewBox="0 0 595.3 367.54"
                width="auto"
                height="200px"
                preserveAspectRatio="xMidYMid slice"
              ></RezeptIcon>
            </div>
          </div>
          <div v-else>
            <img class="recipe-image" :src="item.ImageGallery[0].ImageUrl" />
          </div>
          <div class="grid-info-detail">
            <div class="grid-title">{{ getTitle(item, language) }}</div>
            <div class="grid-short-info">{{ getRecipeShortInfo(item) }}</div>
            <hr class="recipe-divider" />
            <div class="recipe-info-groups">
              <div class="recipe-info-group">
                <PreparationTimeIcon></PreparationTimeIcon>
                <span class="recipe-info-text">{{
                  getPreparationTime(item)
                }}</span>
              </div>
              <div class="recipe-info-group">
                <PersonsIcon></PersonsIcon>
                <span class="recipe-info-text"
                  >{{ getPersonCount(item) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </template>
    <div v-else-if="isLoading" class="loading-spinner">
      <spinner></spinner>
    </div>
    <div class="noResult" v-else>{{ $t('noResults') }}</div>
    <div v-if="items.length === 1" class="item-container"></div>
    <div class="bottom-divider" v-if="items.length > 0 && showList">
      <hr class="solid" />
    </div>
    <div
      class="bottom-divider bottom-divider2"
      v-if="items.length > 1 && showList"
    >
      <hr class="solid" />
    </div>
    <paging
      :current-page="currentPage"
      :total-pages="totalPages"
      @next-page="nextPage"
      @last-page="lastPage"
      @go-to-page="goToPage"
    ></paging>
  </div>
</template>

<script>
import { ArticleApi } from '@/api';
import Paging from '@/components/Paging';
import ListBlueIcon from '@/assets/img/list_blue.svg';
import ListIcon from '@/assets/img/list.svg';
import GridBlueIcon from '@/assets/img/grid_blue.svg';
import GridIcon from '@/assets/img/grid.svg';
import SearchIcon from '@/assets/img/ic_search.svg';
import ArrowIconRight from '@/assets/img/arrow_right.svg';
import RezeptIcon from '@/assets/img/rezept.svg';
import PreparationTimeIcon from '@/assets/img/ic_preparationtime.svg';
import PersonsIcon from '@/assets/img/ic_persons.svg';
import Spinner from '@/components/Spinner';

export default {
  components: {
    Paging,
    ListBlueIcon,
    ListIcon,
    GridBlueIcon,
    GridIcon,
    SearchIcon,
    ArrowIconRight,
    RezeptIcon,
    PreparationTimeIcon,
    PersonsIcon,
    Spinner,
  },
  props: {
    language: {
      type: String,
      default: 'en',
    },
    contentIdList: {
      type: String,
      default: null,
    },
    pageSize: {
      type: Number,
      default: 2,
    },
    category: {
      type: String,
      default: null,
    },
    currentPage: {
      type: Number,
      default: 1,
    },
    defaultPresentation: {
      type: String,
      default: 'list',
    },
  },
  data() {
    return {
      items: [],
      totalPages: 0,
      isLoading: false,
      showList: this.defaultPresentation === 'list',
      searchInput: '',
      timer: null,
    };
  },
  watch: {
    defaultPresentation(newValue) {
      if (newValue === 'list') {
        this.showList = true;
      } else {
        this.showList = false;
      }
    },
  },
  created() {
    this.loadRecipeList(this.currentPage);
  },
  computed: {},
  methods: {
    nextPage() {
      this.items = [];
      this.loadRecipeList(this.currentPage + 1);
      this.$emit('change-current-page', this.currentPage + 1);
    },
    lastPage() {
      this.items = [];
      this.loadRecipeList(this.currentPage - 1);
      this.$emit('change-current-page', this.currentPage - 1);
    },
    goToPage(pageNum) {
      this.items = [];
      this.loadRecipeList(pageNum);
      this.$emit('change-current-page', pageNum);
    },
    showDetail(contentId) {
      this.$emit('show-detail', contentId);
    },
    searchRecipeList() {
      clearTimeout(this.timer);
      this.timer = setTimeout(this.loadRecipeList, 300, 1);
    },
    loadRecipeList(pageNum) {
      this.isLoading = true;
      const articleApi = new ArticleApi();
      articleApi
        .articleGetArticleList(
          pageNum,
          this.pageSize,
          32,
          null,
          this.contentIdList,
          this.language,
          null,
          null,
          null,
          true,
          null,
          '',
          this.language,
          null,
          this.searchInput,
          []
        )
        .then((value) => {
          this.isLoading = false;
          this.items = value?.data?.Items ?? [];
          if (this.items != null) {
            this.items = this.items.filter(
              (item) =>
                item?.Detail?.[this.language] != null &&
                item?.Detail?.[this.language]?.Title != null &&
                item?.Detail?.[this.language]?.Title !== ''
            );
          }
          this.$emit('change-current-page', value?.data?.CurrentPage);
          this.totalPages = value?.data?.TotalPages;
        });
    },
    getTitle(item, language) {
      return item?.Detail?.[language]?.Title ?? '';
    },
    getRecipeShortInfo(item) {
      return item?.Detail[this.language].IntroText ?? '';
    },
    setShowList(active) {
      this.showList = active;
    },
    getPersonCount(item) {
      return (
        item?.AdditionalArticleInfos[this.language]?.Elements?.personen || ''
      );
    },
    getPreparationTime(item) {
      let time =
        item?.AdditionalArticleInfos[this.language]?.Elements?.zeit || '-';
      if (time !== '-') {
        time = time.toString() + ' h';
      }
      return time;
    },
  },
};
</script>
<style scoped lang="scss">
*,
*:before,
*:after {
  box-sizing: border-box;
}

.list-item {
  display: flex;
  flex-direction: row;
  padding: 8px 20px 8px 20px;
  align-items: center;
}

.thumbnail {
  min-height: 60px;
  min-width: 60px;
  max-width: 60px;
  max-height: 60px;
  background-color: #e8ecf1;
  object-fit: cover;
}

.info {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  padding-left: 24px;
}

.info-grid {
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  margin: 16px;
  box-shadow: 0px 3px 6px #00000029;
  cursor: pointer;
}

hr.solid {
  border-top: 1px solid #e8ecf1;
  margin: 8px 20px 8px 20px;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.grid-title {
  font-size: 24px;
  font-weight: bold;
  flex-grow: 1;
}

.short-info {
  font-size: 14px;
  word-break: break-word;
  color: #888888;
}

.grid-short-info {
  font-size: 18px;
  word-wrap: break-word;
  text-overflow: ellipsis;
  color: #888888;
  flex-grow: 1;
  max-height: 150px;
  overflow: hidden;
}

.page-title {
  font-weight: bold;
  font-size: 36px;
  padding-left: 20px;
  padding-right: 20px;
  width: 100%;
  text-overflow: ellipsis;
  overflow: hidden;
}

.list {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  width: 100%;
}

.item-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  cursor: pointer;
}

.bottom-divider {
  width: 100%;
}

.bottom-divider2 {
  visibility: hidden;
}

.grid-container {
  display: flex;
  flex-direction: column;
  width: 100%;
}

@media (min-width: 768px) {
  .item-container {
    width: 50%;
  }

  .bottom-divider {
    width: 50%;
  }

  .bottom-divider2 {
    visibility: visible;
  }

  .grid-container {
    width: 50%;
  }
}

@media (min-width: 1024px) {
  .grid-container {
    width: 33%;
  }
}

@media (min-width: 1400px) {
  .grid-container {
    width: 25%;
  }
}

.loading-spinner {
  height: 70vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.noResult {
  display: flex;
  flex-direction: row;
  width: 100%;
  justify-content: center;
  margin-top: 20px;
}

.grid-info-detail {
  padding: 16px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.recipe-divider {
  border-top: 1px solid #e8ecf1;
  margin-top: 16px;
  margin-bottom: 16px;
  width: 100%;
}

.recipe-info-text {
  padding-left: 8px;
  font-size: 14px;
  color: #212529;
}

.recipe-info-group {
  display: flex;
  width: 50%;
  align-items: center;
}

.recipe-info-groups {
  display: flex;
  flex-direction: row;
}

.recipe-image {
  max-width: 100%;
  height: 200px;
  width: 100%;
  object-fit: cover;
}

.header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  align-items: center;
  padding-right: 20px;
}

.list-grid-switch {
  display: flex;
  flex-direction: row;
  justify-content: center;
}

.list-switch-button {
  width: 60px;
  height: 30px;
  border: solid 1px #e8ecf1;
  border-radius: 15px 0 0 15px;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

.grid-switch-button {
  width: 60px;
  height: 30px;
  border: solid 1px #e8ecf1;
  border-left: 0;
  border-radius: 0 15px 15px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
}

::placeholder {
  color: #1f1f1f80;
}

input:focus {
  outline: none;
}

.search-bar {
  width: 100%;
  margin: 16px;
  display: flex;
  border: 1px solid #e8ecf1;
  align-items: center;
}

.search-input {
  width: 100%;
  padding: 10px;
  border: none;
  flex-grow: 1;
}

.search-button {
  padding-top: 4px;
  padding-right: 20px;
  cursor: pointer;
  min-width: 24px;
  max-width: 24px;
  min-height: 24px;
  max-height: 24px;
}

.grid-icon {
  path {
    fill: #888;
  }
}

.grid-icon-blue {
  path {
    fill: #2980b9;
  }
}

.rezept-icon {
  width: 100%;
  .a {
    fill: none;
  }
  .b {
    fill: #e8ecf1;
  }
  .c {
    clip-path: url(#a);
  }
  .d {
    fill: #fff;
  }
}

.arrow-icon {
  min-height: 40px;
  min-width: 40px;
  max-height: 40px;
  max-width: 40px;
  display: flex;
}
</style>
