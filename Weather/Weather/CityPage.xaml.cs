using Xamarin.Forms;
using Xamarin.Forms.Xaml;

namespace Weather
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class CityPage : TabbedPage
    {
        public CityPage()
        {
            InitializeComponent();
        }
    }
}