<template>
  <div class="list">
    <h2 class="page-title">{{ $t('recipes') }}</h2>
    <paging
        :current-page="currentPage"
        :total-pages="totalPages"
        @next-page="nextPage"
        @last-page="lastPage"
        @go-to-page="goToPage"></paging>
    <template v-if="items.length > 0">
      <div v-for="item of items" :key="item.id" @click.prevent="showDetail(item.Id)" class="item-container">
        <hr class="solid">
        <div class="list-item">
          <div v-if="item.ImageGallery === null || item.ImageGallery.length === 0">
            <img class="thumbnail" :src="placeholderImage"/>
          </div>
          <div v-else><img class="thumbnail" :src="item.ImageGallery[0].ImageUrl"/></div>
          <div class="info">
            <div class="title">{{ getTitle(item, language) }}</div>
            <div class="short-info">{{ getGastronomyShortInfo(item) }}</div>
          </div>
          <img src="@/assets/img/arrow_right.svg" width="28" height="28"/>
        </div>
      </div>
    </template>
    <div v-else-if="isLoading" class="loading-spinner">
      <img src="@/assets/img/loading.gif"/>
    </div>
    <div class="noResult" v-else>{{ $t('noResults')}}</div>
    <div v-if="items.length === 1" class="item-container"></div>
    <div class="bottom-divider" v-if="items.length > 0"><hr class="solid"></div>
    <div class="bottom-divider bottom-divider2" v-if="items.length > 1"><hr class="solid"></div>
    <paging
        :current-page="currentPage"
        :total-pages="totalPages"
        @next-page="nextPage"
        @last-page="lastPage"
        @go-to-page="goToPage"></paging>
  </div>
</template>

<script>
import {ActivityApi, ArticleApi, GastronomyApi, PoiApi} from "@/api";
import Paging from "@/components/Paging";

export default {
  components: {Paging},
  props: {
    language: {
      type: String,
      default: 'en'
    },
    contentIdList: {
      type: String,
      default: null
    },
    pageSize: {
      type: Number,
      default: 2
    },
    category: {
      type: String,
      default: null
    },
    currentPage: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {
      items: [],
      gastronomyTypes: [],
      activityTypes: [],
      totalPages: 0,
      isLoading: false
    };
  },
  created() {
    this.loadRecipeList(1)
  },
  computed: {
    placeholderImage() {
      return require('../assets/img/rezept.svg')
    }
  },
  methods: {
    nextPage() {
      this.items = []
      this.loadRecipeList(this.currentPage + 1)
      this.$emit('change-current-page', this.currentPage + 1);
    },
    lastPage() {
      this.items = []
      this.loadRecipeList(this.currentPage - 1)
      this.$emit('change-current-page', this.currentPage - 1);
    },
    goToPage(pageNum) {
      this.items = []
      this.loadRecipeList(pageNum)
      this.$emit('change-current-page', pageNum);
    },
    showDetail(contentId) {
      this.$emit('show-detail', contentId);
    },
    loadRecipeList(pageNum) {
      this.isLoading = true;
      const articleApi = new ArticleApi()
      articleApi.articleGetArticleList(pageNum, this.pageSize, 32, null, null,
          this.contentIdList,null,null,null,true,null,null,
          this.language,null,null,[]).then((value => {
        this.items = value?.data?.Items ?? []
        console.log(this.items)
        if(this.items != null) {
          this.items = this.items.filter((item) => item?.Detail?.[this.language] != null &&
              item?.Detail?.[this.language]?.Title != null && item?.Detail?.[this.language]?.Title !== "")
        }
        this.$emit('change-current-page', value?.data?.CurrentPage);
        this.totalPages = value?.data?.TotalPages
        console.log(value)
        this.isLoading = false;
      }))
    },
    getTitle(item, language) {
      return item?.Detail?.[language]?.Title ?? ''
    },
    getGastronomyShortInfo(item) {
      return ''
      /*const shortInfo = []
      shortInfo.push(...this.getGastronomyTypes(item))
      if(item?.ContactInfos?.[this.language]?.City) {
        const location = this.$t('location') + ': ' + (item.ContactInfos?.[this.language].City)
        shortInfo.push(location)
      }
      if(item?.ContactInfos?.en?.Phonenumber) {
        const telephone = this.$t('phone') + ': ' + (item.ContactInfos.en.Phonenumber)
        shortInfo.push(telephone)
      }
      if(item?.ContactInfos?.en?.Url){
        const url = this.$t('web') + ': ' + (item.ContactInfos.en.Url)
        shortInfo.push(url)
      }
      return shortInfo.filter((info) => info != null).join(', ')*/
    },
  },
};
</script>
<style scoped>
  .list-item {
    display: flex;
    flex-direction: row;
    padding: 8px 40px 8px 40px;
    align-items: center;
  }

  .thumbnail {
    min-height: 60px;
    min-width: 60px;
    max-width: 60px;
    max-height: 60px;
    background-color: #E8ECF1;
    object-fit: cover;
  }

  .info {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    padding-left: 24px;
  }

  hr.solid {
    border-top: 1px solid #CFCFCF;
    margin: 8px 40px 8px 40px;
  }

  .title {
    font-size: 18px;
    font-weight: bold;
  }

  .short-info {
    font-size: 14px;
    word-break: break-word;
    color: #888888;
  }

  .page-title {
    font-weight: bold;
    font-size: 36px;
    padding-left: 40px;
    padding-right: 40px;
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
  }

  .bottom-divider {
    width: 100%;
  }

  .bottom-divider2 {
    visibility: hidden;
  }

  @media(min-width: 768px){
    .item-container{
      width: 50%;
    }

    .bottom-divider {
      width: 50%;
    }

    .bottom-divider2 {
      visibility: visible;
    }
  }

  .loading-spinner {
    height: 50vh;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .noResult {
    margin-left: 40px;
  }
</style>