using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Weather.Models;
using Weather.Utils;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class CityPage : TabbedPage
    {
        private City city;
        public CityPage(City city)
        {
            this.city = city;
            InitializeComponent();    
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();

            if (IsBusy)
                return;
            try
            {
                HttpService Client = new HttpService();
                ForecastData data = await Client.getForecast(Client.generateUri("forecast", this.city.Name));
                city.Forecast = data;
                await city.setForecastData(data);
            }
            catch (Exception e)
            {
                await Application.Current.MainPage.DisplayAlert(
                    "An error occurred",
                    e.Message,
                    "Ok"
                );
            }
            finally
            {
                IsBusy = false;
            }

            this.Children.Add(new TodayPage(this.city));
            this.Children.Add(new TomorrowPage(this.city));
            this.Children.Add(new FiveDaysPage(this.city));
        }
    }
}