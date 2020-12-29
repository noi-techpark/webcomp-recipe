<template>
  <div>
    <div class="back-button" @click.prevent="close" v-if="isListAvailable">
      <img src="@/assets/img/arrow_left.svg" />
      <span style="color: #888888">{{ $t('back') }}</span>
    </div>
    <div v-if="item" class="item">
      <h1>{{ itemDetail.Title }}</h1>

      <div class="detail-box">
        <div class="recipe-info-group">
          <img src="@/assets/img/ic_preparationtime.svg" />
          <span class="recipe-info-text"
            >{{ $t('preparationTime') }}: {{ preparationTime }}</span
          >
        </div>
        <div class="recipe-info-group">
          <img src="@/assets/img/ic_persons.svg" />
          <span class="recipe-info-text"
            >{{ $t('persons') }}: {{ personCount }}
          </span>
        </div>
      </div>

      <div style="display: flex; flex-direction: row; flex-wrap: wrap;">
        <div class="text content-box">
          <img v-if="titleImage" :src="titleImage" class="header-image" />
          <div v-html="articleText"></div>
        </div>
        <div class="ingredients-container">
          <div class="ingredients-box">
            <div class="ingredient-title">{{ $t('ingredients') }}</div>
            <div v-html="ingredients"></div>
          </div>
        </div>
      </div>

      <div v-if="Object.keys(itemProps).length" class="additional-props-box">
        <ul
          class="props"
          :class="{ single: Object.keys(itemProps).length === 1 }"
        >
          <li v-for="(value, key) of itemProps" :key="key" class="text">
            <span class="prop-key">{{ $t(`props.${key}`) }}:</span>
            {{ value === true ? $t('yes') : value }}
          </li>
        </ul>
      </div>

      <div style="display: flex; align-items: center; flex-direction: column">
        <div class="divider"></div>
        <div class="tags">
          <div v-for="(value, i) of tags" :key="i" class="category">
            {{ value }}
          </div>
        </div>
        <div class="divider"></div>
      </div>

      <div v-if="imageGallery">
        <img
          v-for="(image, i) of imageGallery"
          :key="i"
          :src="image"
          height="200"
          width="200"
          class="image"
          @click="openImageDetail(image)"
        />
      </div>

      <small class="text">
        {{ $t('lastChange') }}: {{ item.LastChange | dateFormat }}
      </small>
    </div>
    <image-detail
      :imgUrl="imageUrl"
      v-if="showImage"
      :hasMultipleImgs="hasMultipleImgs"
      @close="closeImageDetail"
      @next-image="nextImage"
      @last-image="lastImage"
    ></image-detail>
  </div>
</template>

<script>
import { ArticleApi, ODHTagApi } from '@/api';
import ImageDetail from '@/components/ImageDetail';

export default {
  components: { ImageDetail },
  props: {
    contentId: {
      type: String,
      required: true,
    },
    contentType: {
      type: String,
      required: true,
    },
    isListAvailable: {
      type: Boolean,
      default: false,
    },
    language: {
      type: String,
      default: 'de',
    },
  },
  data() {
    return {
      item: null,
      odhTags: [],
      showImage: false,
      imageUrl: null,
      selectedImage: null,
    };
  },
  computed: {
    titleImage() {
      const image = this.item?.ImageGallery[0];
      return image?.ImageUrl || null;
    },
    imageGallery() {
      return this.item?.ImageGallery || [];
    },
    hasMultipleImgs() {
      return this.imageGallery.length > 1;
    },
    itemDetail() {
      return this.item?.Detail?.[this.language] || {};
    },
    articleText() {
      return (
        this.item?.AdditionalArticleInfos?.[this.language]?.Elements
          ?.zubereitungstext || ''
      );
    },
    itemProps() {
      if (!this.item) {
        return {};
      }

      const showProps = [
        'IsOpen',
        'AltitudeSumUp',
        'AltitudeSumDown',
        'HasFreeEntrance',
        'BikeTransport',
        'LiftAvailable',
        'RunToValley',
        'IsWithLight',
        'HasRentals',
        'IsPrepared',
        'FeetClimb',
      ];

      const props = {};
      for (const key of showProps) {
        if (!!this.item[key] && this.item[key] !== '0.0') {
          props[key] = this.item[key];
        }
      }

      return props;
    },
    personCount() {
      return (
        this.item?.AdditionalArticleInfos[this.language]?.Elements?.personen ||
        ''
      );
    },
    preparationTime() {
      return (
        this.item?.AdditionalArticleInfos[this.language]?.Elements?.zeit || '-'
      );
    },
    ingredients() {
      return this.item?.AdditionalArticleInfos[this.language]?.Elements?.zutat1;
    },
    tags() {
      return this.item?.ODHTags.map((e) => {
        return (
          this.odhTags.find((x) => x.Id === e.Id)?.TagName?.[this.language] ||
          ''
        );
      });
    },
  },
  created() {
    this.loadRecipeItem();
    this.loadODHTags(this.language);
  },
  filters: {
    dateFormat(dateString) {
      return new Date(dateString).toLocaleDateString();
    },
  },
  methods: {
    loadODHTags(language) {
      new ODHTagApi()
        .oDHTagGetODHTags(language, null, null, [])
        .then((value) => {
          this.odhTags = value.data;
        });
    },
    loadRecipeItem() {
      new ArticleApi()
        .articleGetActivitySingle(this.contentId)
        .then((value) => {
          this.item = value.data;
        });
    },
    close() {
      this.$emit('close');
    },
    openImageDetail(image) {
      this.imageUrl = image.ImageUrl;
      console.log(this.imageUrl);
      this.selectedImage = image;
      this.showImage = true;
    },
    closeImageDetail() {
      this.showImage = false;
    },
    nextImage() {
      const currentIndex = this.imageGallery.indexOf(this.selectedImage);
      if (currentIndex + 1 < this.imageGallery.length) {
        this.selectedImage = this.imageGallery[currentIndex + 1];
        this.imageUrl = this.selectedImage.ImageUrl;
      } else {
        this.selectedImage = this.imageGallery[0];
        this.imageUrl = this.selectedImage.ImageUrl;
      }
    },
    lastImage() {
      const currentIndex = this.imageGallery.indexOf(this.selectedImage);
      if (currentIndex - 1 >= 0) {
        this.selectedImage = this.imageGallery[currentIndex - 1];
        this.imageUrl = this.selectedImage.ImageUrl;
      } else {
        this.selectedImage = this.imageGallery[this.imageGallery.length - 1];
        this.imageUrl = this.selectedImage.ImageUrl;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.item > div {
  margin-bottom: 1.5rem;
}
.title {
  background-color: #e8ecf1;
  padding: 2rem;
  opacity: 0.8;
  max-width: 100%;
  max-height: 400px;
}

h2 {
  margin-bottom: 2px;
}
.detail-box {
  border: 1px solid #e8ecf1;
  height: 40px;
  border-radius: 20px;
  padding-left: 30px;
  padding-right: 30px;
  display: flex;
  flex-direction: row;
}
.additional-props-box {
  padding: 2rem;
  background-color: #e8ecf1;
}
.props {
  list-style: none;
  padding: 0;
  margin: 0;
  font-style: italic;

  &:not(.single) {
    columns: 1;
  }

  @media (min-width: 768px) {
    &:not(.single) {
      columns: 2;
    }
  }

  @media (min-width: 992px) {
    &:not(.single) {
      columns: 3;
    }
  }

  > li {
    padding: 0.2rem 0;
  }

  img {
    height: 16px;
    width: 16px;
    margin-right: 0.7em;
  }

  a {
    color: #2980b9;
    text-decoration: none;
  }

  .prop-key {
    color: #888888;
  }
}

.subtitle {
  color: #212529;
  font-size: 18px;
  font-weight: bold;
  padding-top: 4px;
  padding-bottom: 4px;
}

.subtype {
  padding-top: 4px;
  padding-bottom: 8px;
}

.categories {
  padding-bottom: 8px;
}

.category {
  padding: 4px 12px 4px 12px;
  margin-right: 8px;
  margin-top: 8px;
  height: 30px;
  border: 1px solid #cfcfcf;
  border-radius: 30px;
  opacity: 1;
  text-align: center;
  align-items: center;
  display: flex;
}

ul {
  padding-inline-start: 0px;
}

.text {
  color: #949494;
}

.text-dark {
  color: #2e3131;
}

.image {
  padding-right: 8px;
  padding-top: 8px;
  object-fit: cover;
}

.title-image {
  max-height: 400px;
  object-fit: cover;
  width: 100%;
}

.title-container {
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  margin-bottom: 0 !important;
  overflow: hidden;
}

.back-button {
  display: flex;
  align-items: center;
  padding-bottom: 4px;
  cursor: pointer;
}

h1 {
  font-size: 36px;
  overflow-wrap: break-word;
  hyphens: auto;
}

.recipe-info-group {
  display: flex;
  width: 50%;
  align-items: center;
}

.recipe-info-text {
  padding-left: 8px;
  font-size: 14px;
  color: #212529;
  overflow-wrap: break-word;
}

.ingredient-title {
  font-size: 28px;
  font-weight: bold;
  color: #2e3131;
}

.ingredients-container {
  width: 100%;
  margin-top: 16px;
}

.ingredients-box {
  background-color: #e8ecf1;
  padding: 25px 16px 25px 25px;
}

.content-box {
  width: 100%;
}

@media (min-width: 768px) {
  .ingredients-container {
    width: 33%;
    margin-top: 0;
  }

  .ingredients-box {
    margin-left: 32px;
  }

  .content-box {
    width: 66%;
  }
}

.category {
  padding: 4px 12px 4px 12px;
  margin-right: 8px;
  margin-top: 8px;
  margin-bottom: 8px;
  height: 30px;
  border: 1px solid #e8ecf1;
  border-radius: 30px;
  opacity: 1;
  text-align: center;
  align-items: center;
  display: flex;
  color: #cfcfcf;
}

.tags {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
}

.divider {
  background-color: #e8ecf1;
  height: 1px;
  width: 300px;
  margin-top: 16px;
  margin-bottom: 16px;
}

.header-image {
  height: 430px;
  max-width: 100%;
}

div ::v-deep ul {
  padding-inline-start: 20px;
}
</style>
