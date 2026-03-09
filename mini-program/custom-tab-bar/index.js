Component({
  data: {
    value: '/pages/index/index',
    themeInfo: wx.getSystemInfoSync().theme || 'light', // 获取初始主题色
    refresh: true, // 控制组件强刷新的开关
    list: [
      { value: '/pages/index/index', label: '首页', icon: 'home' },
      { value: '/pages/history/history', label: '历史', icon: 'history' },
      { value: '/pages/profile/profile', label: '我的', icon: 'user' }
    ]
  },

  lifetimes: {
    attached() {
      // 监听系统主题变化强行触发重新渲染
      wx.onThemeChange((res) => {
        this.setData({ refresh: false }, () => {
          this.setData({ 
            themeInfo: res.theme,
            refresh: true 
          }, () => {
            this.init();
          });
        });
      });
    }
  },

  methods: {
    onChange(e) {
      const { value } = e.detail;
      wx.switchTab({
        url: value,
      });
    },
    init() {
      const page = getCurrentPages().pop();
      const route = page ? page.route : '';
      this.setData({
        value: '/' + route
      });
    }
  }
});
