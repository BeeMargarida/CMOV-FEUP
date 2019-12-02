using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using Weather.Models;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class TodayPage : ContentPage
    {
        private City city;
        public TodayPage(City city)
        {
            InitializeComponent();
            this.city = city;
            this.BindingContext = city;
        }
    }
}