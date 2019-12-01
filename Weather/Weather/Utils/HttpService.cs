using System;
using System.Diagnostics;
using System.Net.Http;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace Weather.Utils
{
    class HttpService
    {
        HttpClient client;

        public HttpService()
        {
            client = new HttpClient();
        }

        public async Task<ResultData> getData(string uri)
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
            }
            catch (Exception ex)
            {
                Debug.WriteLine("\tERROR {0}", ex.Message);
                return null;
            }

            return data;
        }

        public string generateUri(string city)
        {
            return Constants.OpenWeatherMapEndpoint + "?q=" + city + ",pt&units=metric&appid=" + Constants.OpenWeatherMapAPIKey;
        }
    }
}
