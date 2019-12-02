using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Weather.Models;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class CityPage : TabbedPage
    {
        private City city;
        public CityPage(City city)
        {
            InitializeComponent();
            this.city = city;
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();

            //listView.ItemsSource = new List<City>();

            if (IsBusy)
                return;
            try
            {
               
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
        }
    }
}