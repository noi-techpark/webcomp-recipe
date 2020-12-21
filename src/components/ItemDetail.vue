<template>
  <div>
    <a v-if="isListAvailable" href @click.prevent="close">Zurück</a><br />
    <div v-if="item" class="item">
      <div class="title-container" :style="titleImage">
        <div class="title" >
          <h2>{{ itemDetail.Title }}</h2>
        </div>
      </div>

      <div class="detail-box">

        <ul class="props">
          <li v-if="item.Difficulty">
            <img src="@/assets/img/ic_difficulty.svg" />
            <span class="prop-key">{{ $t('difficulty') }}:</span>
            <span class="text-dark">{{ item.Difficulty }}</span>
          </li>
          <li v-if="item.Altitude">
            <img src="@/assets/img/ic_altitudedifference.svg" />
            <span class="prop-key">{{ $t('altitude') }}: </span>
            <span class="text-dark">{{ item.Altitude }}{{ item.AltitudeUnitofMeasure }}</span>
          </li>
          <li v-if="item.AltitudeDifference">
            <img src="@/assets/img/ic_altitudedifference.svg" />
            <span class="prop-key">{{ $t('props.AltitudeDifference') }}: </span>
            <span class="text-dark">{{ item.AltitudeDifference }}{{ item.AltitudeUnitofMeasure }}</span>
          </li>
          <li v-if="item.AltitudeHighestPoint">
            <img src="@/assets/img/ic_altitudehighestpoint.svg" />
            <span class="prop-key"
            >{{ $t('props.AltitudeHighestPoint') }}: </span
            >
            <span class="text-dark">{{ item.AltitudeHighestPoint }}{{ item.AltitudeUnitofMeasure }}</span>
          </li>
          <li v-if="item.AltitudeLowestPoint">
            <img src="@/assets/img/ic_altitudelowestpoint.svg" />
            <span class="prop-key">{{ $t('props.AltitudeLowestPoint') }}: </span>
            <span class="text-dark">{{ item.AltitudeLowestPoint }}{{ item.AltitudeUnitofMeasure }}</span>
          </li>
          <li v-if="item.DistanceDuration">
            <img src="@/assets/img/ic_distanceduration.svg" />
            <span class="prop-key">{{ $t('props.DistanceDuration') }}: </span>
            <span class="text-dark">{{ item.DistanceDuration }}</span>
          </li>
          <li v-if="item.DistanceLength">
            <img src="@/assets/img/ic_distancelength.svg" />
            <span class="prop-key">{{ $t('props.DistanceLength') }}: </span>
            <span class="text-dark">{{ item.DistanceLength }}</span>
          </li>
          <li v-if="googleMapsLink">
            <img src="@/assets/img/ic_map.svg" />
            <a :href="googleMapsLink" target="_blank">Google Maps</a>
          </li>
          <li v-if="itemContactInfos.City">
            <img src="@/assets/img/ic_map.svg" />
            <span class="prop-key">{{ $t('location') }}: </span>
            <span class="text-dark">{{ itemContactInfos.City }}</span>
          </li>
          <li v-if="itemContactInfos.Url">
            <img src="@/assets/img/ic_external-link.svg" />
            <span class="prop-key">{{ $t('web') }}: </span>
            <a :href="itemContactInfos.Url" target="_blank">
              {{ itemContactInfos.Url }}
            </a>
          </li>
          <li v-if="itemContactInfos.Phonenumber">
            <img src="@/assets/img/ic_phone.svg" />
            <span class="prop-key">{{ $t('phone') }}: </span>
            <span class="text-dark">{{ itemContactInfos.Phonenumber }}</span>
          </li>
        </ul>
      </div>

      <div v-if="itemDetail.BaseText" v-html="articleText" class="text"></div>

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

      <div v-if="itemCeremonies.length">
        <div class="subtitle">{{ $t('ceremonies') }}</div>
        <ul>
          <li v-for="(value, i) of itemCeremonies" :key="i" class="text">
            <span class="text-dark">{{ value.name }}</span> (max. {{ value.maxSeatingCapacity }} Personen)
          </li>
        </ul>
      </div>

      <div v-if="itemDishRates.length">
        <div class="subtitle">{{ $t('dishRates') }}</div>
        <ul>
          <li v-for="(value, i) of itemDishRates" :key="i" class="text">
            <span class="text-dark">{{ value.name }}</span> (von {{ value.minAmount }} bis
            {{ value.maxAmount }} {{ value.currencyCode }})
          </li>
        </ul>
      </div>

      <div v-for="type of itemGastronomyTypes" :key="type.type">
        <div class="subtitle">{{ type.name }}</div>
        <div class="gastronomyTypes">
          <div v-for="(value, i) of type.values" :key="i" class="category text">{{ value }}</div>
        </div>
      </div>

      <div v-if="imageGallery">
        <img v-for="(image, i) of imageGallery" :key="i" :src="image.ImageUrl" height="250" width="250"
             class="image" @click="openImageDetail(image)"/>
      </div>

      <small class="text">
        {{ $t('lastChange') }}: {{ item.LastChange | dateFormat }}
      </small>
    </div>
    <image-detail :imgUrl="imageUrl" v-if="showImage"  :hasMultipleImgs="hasMultipleImgs" @close="closeImageDetail"
                  @next-image="nextImage" @last-image="lastImage"></image-detail>
  </div>
</template>

<script>
import { GastronomyApi, PoiApi, ActivityApi, ArticleApi } from '@/api';
import ImageDetail from "@/components/ImageDetail";

const GASTRONOMY_TYPES = [
  'DishCodes',
  'CuisineCodes',
  'FacilityCodes_CreditCard',
  'FacilityCodes_Equipment',
  'FacilityCodes_QualitySeals',
];

const SCHEDULE_DAYS = [
  'Monday',
  'Tuesday',
  'Wednesday',
  'Thuresday',
  'Friday',
  'Saturday',
  'Sunday',
];

export default {
  components: {ImageDetail},
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
      gastronomyTypes: [],
      showImage: false,
      imageUrl: null,
      selectedImage: null
    };
  },
  computed: {
    titleImage() {
      const image = this.item?.ImageGallery[0]
      if(image == null) {
        return {};
      } else {
        return {
          backgroundImage: 'url('+ image.ImageUrl + ') ',
          height: '400px',
          backgroundSize: 'cover',
          backgroundPosition: 'center'
        }
      }
    },
    imageGallery() {
      return this.item?.ImageGallery || []
    },
    hasMultipleImgs() {
      return this.imageGallery.length>1
    },
    itemDetail() {
      return this.item?.Detail?.[this.language] || {};
    },
    articleText() {
      return this.item?.AdditionalArticleInfos?.de?.Elements?.zubereitungstext || ''
    },
    itemContactInfos() {
      return this.item?.ContactInfos?.[this.language] || {};
    },
    googleMapsLink() {
      return this.item?.Latitude && this.item?.Longitude
          ? `https://www.google.com/maps/search/?api=1&query=${this.item.Latitude},${this.item.Longitude}`
          : null;
    },
    itemAdditionalPoiInfos() {
      const infos = this.item?.AdditionalPoiInfos?.[this.language] || {};
      return infos.MainType || infos.SubType ? infos : null;
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
    itemCategories() {
      return (
          this.item?.CategoryCodes?.map(
              (c) =>
                  this.gastronomyTypes.find((t) => t.Id === c.Id)?.TypeDesc[
                      this.language
                      ]
          ).join(', ') || ''
      );
    },
    itemCeremonies() {
      return (
          this.item?.CapacityCeremony?.map((c) => ({
            name: this.gastronomyTypes.find((t) => t.Id === c.Id)?.TypeDesc[
                this.language
                ],
            maxSeatingCapacity: c.MaxSeatingCapacity,
          })) || []
      );
    },
    itemDishRates() {
      return (
          this.item?.DishRates?.map((c) => ({
            name: this.gastronomyTypes.find((t) => t.Id === c.Id)?.TypeDesc[
                this.language
                ],
            maxAmount: c.MaxAmount,
            minAmount: c.MinAmount,
            currencyCode: c.CurrencyCode,
          })) || []
      );
    },
    itemGastronomyTypes() {
      const filteredArray = this.gastronomyTypes.filter((t) =>
          this.item.Facilities.find((f) => t.Id === f.Id)
      );
      return GASTRONOMY_TYPES.map((type) => ({
        name: this.$t(`gastronomyTypes.${type}`),
        values: filteredArray
            .filter((t) => t.Type === type)
            .map((t) => t.TypeDesc[this.language]),
      })).filter((t) => t.values.length);
    },
    itemOperationSchedule() {
      return this.item.OperationSchedule.filter((s) => {
        const start = new Date(s.Start);
        const stop = new Date(s.Stop);
        const now = new Date();
        return (
            (((s.Type === '1' || s.Type === '2') && s.OperationScheduleTime) ||
                s.Type === '3') &&
            now.getTime() <= stop.getTime() &&
            now.getTime() >= start.getTime()
        );
      });
    },
    getItemScheduleDays() {
      return (scheduleTime) =>
          SCHEDULE_DAYS.map((day) =>
              scheduleTime[day] ? this.$t(`scheduleDays.${day}`) : null
          ).filter((day) => day != null).join(', ');
    },
    personCount() {
      return this.item?.AdditionalArticleInfos[this.language]?.Elements?.personen || ''
    },
    preparationTime() {
      return this.item?.AdditionalArticleInfos[this.language]?.Elements?.zeit || ''
    },
    ingredients() {
      return this.this.item?.AdditionalArticleInfos[this.language]?.Elements?.zutat
    }
  },
  created() {
    this.loadRecipeItem()
  },
  filters: {
    dateFormat(dateString) {
      return new Date(dateString).toLocaleDateString();
    },
  },
  methods: {
    loadRecipeItem() {
      new ArticleApi()
          .articleGetActivitySingle(this.contentId)
          .then((value) => {
            this.item = value.data;
            console.log(this.item)
          });
    },
    loadGastronomyTypeList() {
      new GastronomyApi()
          .gastronomyGetAllGastronomyTypesList()
          .then((value) => {
            this.gastronomyTypes = value.data;
          });
    },
    loadPoiItem() {
      new PoiApi().poiGetPoiSingle(this.contentId).then((value) => {
        this.item = value.data;
      });
    },
    loadActivityItem() {
      new ActivityApi()
          .activityGetActivitySingle(this.contentId)
          .then((value) => {
            this.item = value.data;
          });
    },
    close() {
      this.$emit('close');
    },
    openImageDetail(image) {
      this.imageUrl = image.ImageUrl;
      console.log(this.imageUrl)
      this.selectedImage = image;
      this.showImage = true;
    },
    closeImageDetail(){
      this.showImage = false;
    },
    nextImage() {
      const currentIndex = this.imageGallery.indexOf(this.selectedImage)
      if(currentIndex+1 < this.imageGallery.length) {
        this.selectedImage = this.imageGallery[currentIndex+1]
        this.imageUrl = this.selectedImage.ImageUrl
      } else {
        this.selectedImage = this.imageGallery[0]
        this.imageUrl = this.selectedImage.ImageUrl
      }
    },
    lastImage() {
      const currentIndex = this.imageGallery.indexOf(this.selectedImage)
      if(currentIndex-1 >= 0) {
        this.selectedImage = this.imageGallery[currentIndex-1]
        this.imageUrl = this.selectedImage.ImageUrl
      } else {
        this.selectedImage = this.imageGallery[this.imageGallery.length-1]
        this.imageUrl = this.selectedImage.ImageUrl
      }
    }
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
  width: 100%;
  max-height: 400px;
}

h2 {
  margin-bottom: 2px;
}
.detail-box {
  border: 1px solid #e8ecf1;
  padding: 2rem;
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

  @media(min-width: 768px){
    &:not(.single) {
      columns: 2;
    }
  }

  @media(min-width: 992px){
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

.gastronomyTypes {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

.category{
  padding: 4px 12px 4px 12px;
  margin-right: 8px;
  margin-top: 8px;
  height: 30px;
  border: 1px solid #CFCFCF;
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
  color: #949494
}

.text-dark {
  color: #2E3131;
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

</style>
