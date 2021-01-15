<template>
  <div class="modal">
    <span class="close" @click="closeImage">&times;</span>

    <div
      class="modal-content"
      style="display: flex; align-items: center; justify-content: space-between;"
      :style="imageStyle"
    >
      <div class="img-button" @click="lastImage" v-if="hasMultipleImgs">
        <img src="@/assets/img/arrow_left.svg" width="50" height="50" />
      </div>
      <div class="img-button" @click="nextImage" v-if="hasMultipleImgs">
        <img src="@/assets/img/arrow_right.svg" width="50" height="50" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ImageDetail',
  props: {
    imgUrl: {
      type: String,
    },
    hasMultipleImgs: {
      type: Boolean,
      default: true,
    },
  },
  computed: {
    imageStyle() {
      return {
        backgroundImage: 'url(' + this.imgUrl + ') ',
        backgroundSize: 'contain',
        backgroundPosition: 'center',
        minHeight: '100px',
        backgroundRepeat: 'no-repeat',
      };
    },
  },
  methods: {
    closeImage() {
      this.$emit('close');
    },
    nextImage() {
      this.$emit('next-image');
    },
    lastImage() {
      this.$emit('last-image');
    },
  },
};
</script>

<style scoped>
.modal {
  display: flex;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0, 0, 0);
  background-color: rgba(0, 0, 0, 0.9);
}

.modal-content {
  margin: auto;
  display: block;
  width: 80%;
  max-width: 1200px;
  height: 100%;
}

.img-content {
  max-width: 80%;
}

.modal-content {
  -webkit-animation-name: zoom;
  -webkit-animation-duration: 0.6s;
  animation-name: zoom;
  animation-duration: 0.6s;
}

@-webkit-keyframes zoom {
  from {
    -webkit-transform: scale(0);
  }
  to {
    -webkit-transform: scale(1);
  }
}

@keyframes zoom {
  from {
    transform: scale(0);
  }
  to {
    transform: scale(1);
  }
}

.close {
  position: absolute;
  top: 15px;
  right: 35px;
  color: #f1f1f1;
  font-size: 40px;
  font-weight: bold;
  transition: 0.3s;
}

.close:hover,
.close:focus {
  color: #bbb;
  text-decoration: none;
  cursor: pointer;
}

/* 100% Image Width on Smaller Screens */
@media only screen and (max-width: 700px) {
  .modal-content {
    width: 100%;
  }
  .img-content {
    max-width: 100%;
  }
}

.img-button {
  width: 50px;
  text-align: center;
  background-color: rgba(0, 0, 0, 0.3);
  height: 50px;
  border-radius: 25px;
  margin: 8px;
}

.img-button:hover {
  background-color: rgba(0, 0, 0, 0.7);
}
</style>
