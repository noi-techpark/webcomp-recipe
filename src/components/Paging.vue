<template>
  <div class="paging" v-if="totalPages > 1">
    <div @click="lastPage" class="back" v-if="currentPage !== 1">{{$t('paging.back')}}</div>
    <div v-if="totalPages < 8">
    <span class="item" v-for="page in totalPages" @click="goToPage(page)" :class="{'selected-item': currentPage===page}" :key="page">
      {{page}}
    </span>
    </div>
    <div v-else>
      <div v-if="currentPage < 4">
        <span class="item" @click="goToPage(1)" :class="{'selected-item': currentPage===1}">1</span>
        <span class="item" @click="goToPage(2)" :class="{'selected-item': currentPage===2}">2</span>
        <span class="item" @click="goToPage(3)" :class="{'selected-item': currentPage===3}">3</span>
        <span class="item" @click="goToPage(4)" :class="{'selected-item': currentPage===4}">4</span>
        <span class="item">...</span>
        <span class="item" @click="goToPage(totalPages)">{{totalPages}}</span>
      </div>
      <div v-else-if="currentPage < totalPages-2">
        <span class="item" @click="goToPage(1)">1</span>
        <span class="item">...</span>
        <span class="item" @click="goToPage(currentPage-1)">{{ currentPage - 1 }}</span>
        <span class="item selected-item">{{ currentPage }}</span>
        <span class="item" @click="goToPage(currentPage+1)">{{ currentPage + 1 }}</span>
        <span class="item">...</span>
        <span class="item" @click="goToPage(totalPages)">{{totalPages}}</span>
      </div>
      <div v-else>
        <span class="item" @click="goToPage(1)">1</span>
        <span class="item">...</span>
        <span class="item" @click="goToPage(totalPages-3)" :class="{'selected-item': currentPage===totalPages-3}">{{totalPages-3}}</span>
        <span class="item" @click="goToPage(totalPages-2)" :class="{'selected-item': currentPage===totalPages-2}">{{totalPages-2}}</span>
        <span class="item" @click="goToPage(totalPages-1)" :class="{'selected-item': currentPage===totalPages-1}">{{totalPages-1}}</span>
        <span class="item" @click="goToPage(totalPages)" :class="{'selected-item': currentPage===totalPages}">{{totalPages}}</span>
      </div>
    </div>
    <div @click="nextPage" v-if="currentPage !== totalPages" class="next">{{$t('paging.next')}}</div>
  </div>
</template>

<script>
export default {
  name: "Paging",
  props: {
    currentPage: {
      required: true,
      type: Number
    },
    totalPages: {
      required: true,
      type: Number
    }
  },
  methods: {
    nextPage() {
      this.$emit('next-page');
    },
    lastPage() {
      this.$emit('last-page');
    },
    goToPage(pageNum) {
      this.$emit('go-to-page', pageNum);
    }
  }
}
</script>

<style scoped>
  .paging {
    display: flex;
    width: 100%;
    justify-content: center;
    margin-top: 40px;
    margin-bottom: 40px;
  }

  .item {
    padding-right: 4px;
    padding-left: 4px;
  }
  
  .item:hover {
    cursor: pointer;
  }

  .selected-item {
    color: #2980B9;
  }

  .back {
    margin-right: 20px;
  }

  .back:hover, .next:hover {
    cursor: pointer;
  }

  .next {
    margin-left: 20px;
  }
</style>