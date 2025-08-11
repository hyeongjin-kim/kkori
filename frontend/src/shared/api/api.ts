import axios, { AxiosRequestConfig, AxiosResponse } from 'axios';
export const api = axios.create({
  baseURL: process.env.BASE_URL || '',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

export const get = async <T>(
  url: string,
  config?: AxiosRequestConfig,
): Promise<T> => {
  const res: AxiosResponse<T> = await api.get(url, config);
  return res.data;
};

export const post = async <T>(
  url: string,
  body?: any,
  config?: AxiosRequestConfig,
): Promise<T> => {
  const res: AxiosResponse<T> = await api.post(url, body, config);
  return res.data;
};

export const put = async <T>(
  url: string,
  body?: any,
  config?: AxiosRequestConfig,
): Promise<T> => {
  const res: AxiosResponse<T> = await api.put(url, body, config);
  return res.data;
};

export const patch = async <T>(
  url: string,
  body?: any,
  config?: AxiosRequestConfig,
): Promise<T> => {
  const res: AxiosResponse<T> = await api.patch(url, body, config);
  return res.data;
};

export const del = async <T>(
  url: string,
  config?: AxiosRequestConfig,
): Promise<T> => {
  const res: AxiosResponse<T> = await api.delete(url, config);
  return res.data;
};

export const audioApi = axios.create({
  baseURL: process.env.BASE_URL || '',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
});

export const audioPost = async (url: string, blob: Blob) => {
  const formData = new FormData();
  formData.append('audioFile', blob, 'answer.webm');
  console.log(formData);
  const res = await audioApi.post(url, formData, {
    onUploadProgress: e =>
      console.log('progress', Math.round((e.loaded / e.total!) * 100)),
  });
  return res.data;
};
