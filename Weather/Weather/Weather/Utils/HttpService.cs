using System;
using System.Net.Http;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Weather.Models;

namespace Weather.Utils
{
    class HttpService
    {
        HttpClient client;

        public HttpService()
        {
            client = new HttpClient();
        }

        public async Task<ResultData> getCurrentWeather(string uri)
        {
            ResultData data = null;
            try
            {
                HttpResponseMessage response = await client.GetAsync(uri);
                if (response.IsSuccessStatusCode)
                {
                    string content = await response.Content.ReadAsStringAsync();
                    data = JsonConvert.DeserializeObject<ResultData>(content);
                }
                else
                {
                    return null;
                }
            }
            catch (Exception ex)
            {
                return null;
            }

            return data;
        }

        public async Task<ForecastData> getForecast(string uri)
        {
            ForecastData data = null;
            try
            {
                HttpResponseMessage response = await client.GetAsync(uri);
                if (response.IsSuccessStatusCode)
                {
                    string content = await response.Content.ReadAsStringAsync();
                    data = JsonConvert.DeserializeObject<ForecastData>(content);
                }
                else
                {
                    return null;                }
            }
            catch (Exception ex)
            {
                return null;
            }

            return data;
        }

        public string generateUri(string action, string city)
        {
            return Constants.OpenWeatherMapEndpoint + action + "?q=" + city + ",pt&units=metric&appid=" + Constants.OpenWeatherMapAPIKey;
        }
    }
}
