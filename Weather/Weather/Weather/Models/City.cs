using System;
using System.IO;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;
using SkiaSharp;
using Xamarin.Forms;

namespace Weather.Models
{
    public class City
    {
        public string Name { get; set; }
        public long ID { get; set; }
        public string CurrentWeatherStatus { get; set; }
        public ImageSource CurrentWeatherIcon { get; set; }
        public string Temperature { get; set; }
        public ForecastData Forecast { get; set; }

        public Status Today;
        public Status Tomorrow;
        public List<Status> FiveDays;

        public City(string name)
        {
            this.Name = name;
            this.Today = new Status();
            this.Tomorrow = new Status();
            this.FiveDays = new List<Status>();
        }

        public void setData(ResultData data)
        {
            this.Name = data.Name;
            this.ID = data.ID;
            this.CurrentWeatherStatus = data.Weather[0].Status;
            this.CurrentWeatherIcon = new Uri("http://openweathermap.org/img/wn/" + data.Weather[0].Icon + "@2x.png");
            this.Temperature = Math.Round(data.Main.Temperature).ToString();
            this.Today.setData("Today", data, DateTime.Now);
        }

        public async Task<int> setForecastData(ForecastData data)
        {
            ForecastEntry[] entries = data.Entries;
            List<GraphData> graphDataToday = new List<GraphData>();
            List<GraphData> graphDataTomorrow = new List<GraphData>();
            SortedDictionary<WeatherStatus, int> averageStatusTomorrow = new SortedDictionary<WeatherStatus, int>();

            double maxTemperature = 0;
            double minTemperature = 0;
            string currentWeatherStatus = "";
            string currentWeatherIcon = null;
            double rain = 0;
            double windSpeed = 0;
            long windDegree = 0;
            double temperature = 0;
            long pressure = 0;
            long humidity = 0;
            int n_entries_days = 0;

            foreach (ForecastEntry entry in entries)
            {
                DateTime current = DateTime.Today;
                DateTime date = DateTime.Parse(entry.DateTimeText);
                double dateDiff = (date.Date - current).TotalDays;

                if (dateDiff == 0)
                {
                    GraphData gr = new GraphData(date, Math.Round(entry.Main.MaxTemperature).ToString(), Math.Round(entry.Main.MinTemperature).ToString(), entry.Weather[0].Status, entry.Weather[0].Icon);
                    await gr.setImage(entry.Weather[0].Icon);
                    graphDataToday.Add(gr);
                }
                else if (dateDiff == 1)
                {
                    temperature += entry.Main.Temperature;
                    rain += entry.Rain != null ? entry.Rain.RainVolume : 0;
                    maxTemperature += entry.Main.MaxTemperature;
                    minTemperature += entry.Main.MinTemperature;
                    pressure += entry.Main.Pressure;
                    humidity += entry.Main.Humidity;
                    windSpeed += entry.Wind.Speed;
                    windDegree += entry.Wind.Degree;
                    n_entries_days += 1;
                       
                    // Saving status for tomorrow to later check what was the most common
                    if(averageStatusTomorrow.ContainsKey(entry.Weather[0]))
                    {
                        averageStatusTomorrow[entry.Weather[0]] = averageStatusTomorrow[entry.Weather[0]] + 1;
                    }
                    else
                    {
                        averageStatusTomorrow.Add(entry.Weather[0], 1);
                    }

                    // saving graph data
                    GraphData gr = new GraphData(date, Math.Round(entry.Main.MaxTemperature).ToString(), Math.Round(entry.Main.MinTemperature).ToString(), entry.Weather[0].Status, entry.Weather[0].Icon);
                    await gr.setImage(entry.Weather[0].Icon);
                    graphDataTomorrow.Add(gr);
                }
                else if ((dateDiff >= 2 || dateDiff <= 6) && date.Hour == 12)
                {
                    Status status = new Status();
                    status.setData(entry);
                    this.FiveDays.Add(status);
                }
            }

            // Averages of the data for tomorrow
            rain = rain != 0 ? rain / n_entries_days : 0;
            temperature = temperature / n_entries_days;
            maxTemperature = maxTemperature / n_entries_days;
            minTemperature = minTemperature / n_entries_days;
            pressure = pressure / n_entries_days;
            humidity = humidity / n_entries_days;
            windSpeed = windSpeed / n_entries_days;
            windDegree = windDegree / n_entries_days;

            // Get the most common status in the forecast for tomorrow
            WeatherStatus statusTomorrow = averageStatusTomorrow.Aggregate((left, right) => left.Value > right.Value ? left : right).Key;

            this.Tomorrow.setData("Tomorrow", statusTomorrow.Status, statusTomorrow.Icon, rain, windSpeed, windDegree, temperature, pressure, humidity, minTemperature, maxTemperature, (DateTime.Now).AddDays(1));
            this.Tomorrow.GraphData = graphDataTomorrow;

            this.Today.GraphData = graphDataToday;

            this.FiveDays.Insert(0, this.Tomorrow);
            this.FiveDays.Insert(0, this.Today);

            return 0;
        }

    }

    public class Status
    {
        public string Day { get; set; }
        public string CurrentWeatherStatus { get; set; }
        public ImageSource CurrentWeatherIcon { get; set; }
        public double WindSpeed { get; set; }
        public long WindDegree { get; set; }
        public string Temperature { get; set; }
        public double Rain { get; set; }
        public long Pressure { get; set; }
        public long Humidity { get; set; }
        public string MinTemperature { get; set; }
        public string MaxTemperature { get; set; }
        public List<GraphData> GraphData { get; set; }
        public DateTime Date { get; set; }

        public void setData(string day, ResultData data, DateTime date)
        {
            this.Day = day;
            this.CurrentWeatherStatus = data.Weather[0].Status;
            this.CurrentWeatherIcon = new Uri("http://openweathermap.org/img/wn/" + data.Weather[0].Icon + "@2x.png");
            this.WindSpeed = Math.Round(data.Wind.Speed,1);
            this.WindDegree = data.Wind.Degree;
            this.Temperature = Math.Round(data.Main.Temperature).ToString();
            this.Pressure = data.Main.Pressure;
            this.Humidity = data.Main.Humidity;
            this.MinTemperature = Math.Round(data.Main.MinTemperature).ToString();
            this.MaxTemperature = Math.Round(data.Main.MaxTemperature).ToString();
            this.Date = date;

            if(data.Rain != null)
            {
                this.Rain = data.Rain.RainVolume;
            }
            else
            {
                this.Rain = 0;
            }
        }

        public void setData(ForecastEntry data)
        {
            this.CurrentWeatherStatus = data.Weather[0].Status;
            this.CurrentWeatherIcon = ImageSource.FromUri(new Uri("http://openweathermap.org/img/wn/" + data.Weather[0].Icon + "@2x.png"));
            this.WindSpeed = Math.Round(data.Wind.Speed, 1);
            this.WindDegree = data.Wind.Degree;
            this.Temperature = Math.Round(data.Main.Temperature).ToString();
            this.Pressure = data.Main.Pressure;
            this.Humidity = data.Main.Humidity;
            this.MinTemperature = Math.Round(data.Main.MinTemperature).ToString();
            this.MaxTemperature = Math.Round(data.Main.MaxTemperature).ToString();
            this.Date = DateTime.Parse(data.DateTimeText);

            if (data.Rain != null)
            {
                this.Rain = data.Rain.RainVolume;
            }
            else
            {
                this.Rain = 0;
            }
        }

        public void setData(string day, string currentWeatherStatus, string currentWeatherIcon, double rain, double windSpeed, long windDegree, double temperature, long pressure, long humidity, double minTemperature, double maxTemperature, DateTime date)
        {
            this.Day = day;
            this.CurrentWeatherStatus = currentWeatherStatus;
            this.CurrentWeatherIcon = ImageSource.FromUri(new Uri("http://openweathermap.org/img/wn/" + currentWeatherIcon + "@2x.png"));
            this.Rain = rain;
            this.WindSpeed = Math.Round(windSpeed, 1);
            this.WindDegree = windDegree;
            this.Temperature = Math.Round(temperature).ToString();
            this.Pressure = pressure;
            this.Humidity = humidity;
            this.MinTemperature = Math.Round(minTemperature).ToString();
            this.MaxTemperature = Math.Round(maxTemperature).ToString();
            this.Date = date;
        }
    }

    public class GraphData
    {
        public DateTime DateTime { get; set; }
        public string MaxTemperature { get; set; }
        public string MinTemperature { get; set; }
        public string CurrentWeatherStatus { get; set; }
        public SKBitmap CurrentWeatherIcon { get; set; }

        public GraphData(DateTime dateTime, string maxTemperature, string minTemperature, string currentWeatherStatus, string currentWeatherIcon)
        {
            this.DateTime = dateTime;
            this.MaxTemperature = maxTemperature;
            this.MinTemperature = minTemperature;
            this.CurrentWeatherStatus = currentWeatherStatus;
            //this.CurrentWeatherIcon = SKBitmapImageSource.FromUri(new Uri("http://openweathermap.org/img/wn/" + currentWeatherIcon + "@2x.png"));
        }

        public async Task<int> setImage(string currentWeatherIcon)
        {
            var httpClient = new System.Net.Http.HttpClient();
            var bytes = await httpClient.GetByteArrayAsync("http://openweathermap.org/img/wn/" + currentWeatherIcon + "@2x.png");

            // wrap the bytes in a stream
            var stream = new MemoryStream(bytes);

            // decode the bitmap stream
            this.CurrentWeatherIcon = SKBitmap.Decode(stream);

            return 0;
        }
    }
}
