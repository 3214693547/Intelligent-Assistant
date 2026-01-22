
// 创建axios实例
const instance = axios.create({
    // baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
    baseURL: "http://127.0.0.1:8080", // url = base url + request url
    // baseURL: "http://150.109.247.64:9090",
    // baseURL: "https://www.itzixi.com/api", // url = base url + request url
    withCredentials: true, // send cookies when cross-domain requests
    timeout: 60000 // request timeout
});

// axios请求的拦截器
instance.interceptors.request.use(
    config => {
        // do something before request is sent

        // 从 localStorage 获取 Token
        var token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }

        var userInfo = cookieUtils.getUserInfo();
        // console.log(userInfo);
        if (userInfo) {
            // console.log("userId = " + userInfo.id);
            config.headers['headerUserId'] = userInfo.id;
        }

        var userToken = cookieUtils.getToken();
        // console.log("userToken = " + userToken);
        if (userToken) {
            // console.log("userToken = " + userToken);
            config.headers['headerUserToken'] = userToken;
        }

        return config
    },
    error => {
        // do something with request error
        console.log(error) // for debug
        return Promise.reject(error)
    }
);

// axios响应的拦截器
instance.interceptors.response.use(
    response => {
        const res = response.data
        return res;
    },
    error => {
        console.log('err: ' + error) // for debug
        
        // 处理 401 未授权，跳转到登录页
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('userId');
            localStorage.removeItem('username');
            localStorage.removeItem('nickname');
            
            // 如果不是在登录页，则跳转到登录页
            if (window.location.pathname.indexOf('login.html') === -1) {
                alert('登录已过期，请重新登录');
                window.location.href = 'login.html';
            }
        }
        
        return Promise.reject(error)
    }
)
