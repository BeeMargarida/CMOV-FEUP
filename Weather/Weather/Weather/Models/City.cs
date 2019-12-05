using System;
using System.Diagnostics;
using System.Collections.Generic;
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
            this.Today.setData(data);
        }

        public void setForecastData(ForecastData data)
        {
            ForecastEntry[] entries = data.Entries;
            List<GraphData> graphDataToday = new List<GraphData>();
            List<GraphData> graphDataTomorrow = new List<GraphData>();

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

            //TODO: Check average of status of the weather - maybe use hashmap?

            foreach (ForecastEntry entry in entries)
            {
                DateTime current = DateTime.Today;
                DateTime date = DateTime.Parse(entry.DateTimeText);
                double dateDiff = (date.Date - current).TotalDays;

                if (dateDiff == 0)
                {
                    GraphData gr = new GraphData(current, Math.Round(entry.Main.MaxTemperature).ToString(), Math.Round(entry.Main.MinTemperature).ToString(), entry.Weather[0].Status, entry.Weather[0].Icon);
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

                    // saving graph data
                    GraphData gr = new GraphData(current, Math.Round(entry.Main.MaxTemperature).ToString(), Math.Round(entry.Main.MinTemperature).ToString(), entry.Weather[0].Status, entry.Weather[0].Icon);
                    graphDataTomorrow.Add(gr);
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

            this.Tomorrow.setData(currentWeatherStatus, currentWeatherIcon, rain, windSpeed, windDegree, temperature, pressure, humidity, minTemperature, maxTemperature);
            this.Tomorrow.GraphData = graphDataTomorrow;

            this.Today.GraphData = graphDataToday;
        }

    }

    public class Status
    {
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

        public void setData(ResultData data)
        {
            this.CurrentWeatherStatus = data.Weather[0].Status;
            this.CurrentWeatherIcon = new Uri("http://openweathermap.org/img/wn/" + data.Weather[0].Icon + "@2x.png");
            this.WindSpeed = data.Wind.Speed;
            this.WindDegree = data.Wind.Degree;
            this.Temperature = Math.Round(data.Main.Temperature).ToString();
            this.Pressure = data.Main.Pressure;
            this.Humidity = data.Main.Humidity;
            this.MinTemperature = Math.Round(data.Main.MinTemperature).ToString();
            this.MaxTemperature = Math.Round(data.Main.MaxTemperature).ToString();
            if(data.Rain != null)
            {
                this.Rain = data.Rain.RainVolume;
            }
            else
            {
                this.Rain = 0;
            }
        }

        public void setData(string currentWeatherStatus, string currentWeatherIcon, double rain, double windSpeed, long windDegree, double temperature, long pressure, long humidity, double minTemperature, double maxTemperature)
        {
            this.CurrentWeatherStatus = currentWeatherStatus;
            this.CurrentWeatherIcon = new Uri("http://openweathermap.org/img/wn/" + currentWeatherIcon + "@2x.png");
            this.Rain = rain;
            this.WindSpeed = windSpeed;
            this.WindDegree = windDegree;
            this.Temperature = Math.Round(temperature).ToString();
            this.Pressure = pressure;
            this.Humidity = humidity;
            this.MinTemperature = Math.Round(minTemperature).ToString();
            this.MaxTemperature = Math.Round(maxTemperature).ToString();
        }
    }

    public class GraphData
    {
        public DateTime DateTime { get; set; }
        public string MaxTemperature { get; set; }
        public string MinTemperature { get; set; }
        public string CurrentWeatherStatus { get; set; }
        public ImageSource CurrentWeatherIcon { get; set; }

        public GraphData(DateTime dateTime, string maxTemperature, string minTemperature, string currentWeatherStatus, string currentWeatherIcon)
        {
            this.DateTime = dateTime;
            this.MaxTemperature = maxTemperature;
            this.MinTemperature = minTemperature;
            this.CurrentWeatherStatus = currentWeatherStatus;
            this.CurrentWeatherIcon = new Uri("http://openweathermap.org/img/wn/" + currentWeatherIcon + "@2x.png");
        }
    }
}
