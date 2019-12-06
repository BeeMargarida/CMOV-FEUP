using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Weather.Models;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class FiveDaysPage : ContentPage
    {
        private City city;
        public FiveDaysPage(City city)
        {
            this.city = city;
            InitializeComponent();
            this.BindingContext = this.city.FiveDays;
        }

        protected override async void OnAppearing()
        {
            base.OnAppearing();
            listView.ItemsSource = this.city.FiveDays;
        }
    }
}