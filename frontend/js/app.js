const { createApp } = Vue;

createApp({
  // data()是用來放資料狀態的地方 等於畫面要用的變數都放在這裡
  data() {
    return {
      cities: [],
      districts: [],
      selectedCity: "",
      selectedDistrict: "",
    };
  },
  //  methods是Vue裡面放方法/函式的地方 可以把它想成->放按鈕點擊後要做什麼、事件發生時要做什麼
  methods: {
    loadCities() {
      fetch("http://localhost:8080/ajaxdemo/getCities")
        .then((response) => response.json())
        .then((data) => {
          this.cities = data;
        })
        .catch((error) => {
          console.error("載入縣市失敗：", error);
        });
    },
    loadDistricts() {
      this.selectedDistrict = "";
      this.districts = [];
      // 當沒有選縣市的時候直接return結束這個函式，不要再往下做
      if (!this.selectedCity) return;
      //   fetch 是 JavaScript 內建的 API，用來發送 HTTP 請求。
      fetch(
        // encodeURIComponent： 這非常重要！ 因為網址裡面不能直接放中文（會亂碼），這個函式會把「台北市」轉成電腦看得懂的特殊編碼（例如 %E5%8F%B0%E5%8C%97%E5%B8%82）。
        "http://localhost:8080/ajaxdemo/getDistricts?city=" +
          encodeURIComponent(this.selectedCity),
      )
        .then((response) => response.json())
        .then((data) => {
          this.districts = data;
        })
        .catch((error) => {
          console.error("載入區域失敗：", error);
        });
    },
  },
  mounted() {
    this.loadCities();
  },
}).mount("#app");
