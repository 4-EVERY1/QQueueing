import axios, { AxiosRequestConfig, AxiosRequestHeaders } from "axios";

interface AdaptAxiosRequestConfig extends AxiosRequestConfig {
  headers: AxiosRequestHeaders;
}

const axiosInstance = () => {
  const instance = axios.create({
    baseURL: process.env.baseUrl,
  });

  // 모든 요청에 대해 기본 헤더 속성 설정
  instance.defaults.headers.common["Content-Type"] =
    "application/json; charset=utf8";

  return instance;
};

export default axiosInstance;
