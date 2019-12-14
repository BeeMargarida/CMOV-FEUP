using SkiaSharp;
using SkiaSharp.Views.Forms;
using Weather.Models;

namespace Weather.Utils
{
    class Graph
    {
        public static double MAX_TEMPERATURE = 50;
        public static double MIN_TEMPERATURE = -10;
        public static void Draw(SKPaintSurfaceEventArgs e, Status day)
        {
            SKCanvas canvas = e.Surface.Canvas;

            int width = e.Info.Width;
            int height = e.Info.Height;
            int marginX = (int)(width * 0.05);
            int marginY = (int)(height * 0.20);

            // Paint background
            canvas.Clear(SKColors.Transparent);

            #region Draw Axis
            SKPaint axisPaint = new SKPaint
            {
                Style = SKPaintStyle.Stroke,
                Color = SKColors.White,
                StrokeWidth = 3,
            };

            SKPath axisLines = new SKPath();

            // Both axis
            //axisLines.MoveTo(marginX, marginY);
            //axisLines.LineTo(marginX, height - marginY);
            //axisLines.LineTo(width - marginX, height - marginY);

            // Only X Axis
            axisLines.MoveTo(marginX, height - marginY);
            axisLines.LineTo(width - marginX, height - marginY);

            canvas.DrawPath(axisLines, axisPaint);
            #endregion

            #region Draw Labels
            // Label Color
            SKPaint labelPaint = new SKPaint
            {
                Style = SKPaintStyle.Fill,
                Color = SKColors.White,
                StrokeWidth = 2,
            };
            labelPaint.TextSize = (float)(height / 40);
            SKPaint whitePaint = new SKPaint
            {
                Style = SKPaintStyle.Fill,
                Color = SKColors.White,
                StrokeWidth = 5,
            };
            SKPaint maxStroke = new SKPaint
            {
                Style = SKPaintStyle.Stroke,
                Color = SKColors.White,
                StrokeWidth = 5,
            };
            SKPaint minStroke = new SKPaint
            {
                Style = SKPaintStyle.Stroke,
                Color = SKColors.White,
                StrokeWidth = 5,
            };

            SKPath graphLinesMax = new SKPath();
            SKPath graphLinesMin = new SKPath();

            int n = 0;
            // spacing between the hours, based on the number of points and the width of the screen
            int spacing = day.GraphData.Count == 2 ? width - 3*marginX:  (width - 2*marginX) / day.GraphData.Count;
            foreach(GraphData point in day.GraphData)
            {
                float x = marginX + (float)n * spacing - 10;
                float y = height - marginY / 2 - 30;
                SKPoint label = new SKPoint
                {
                    X = x,
                    Y = height - marginY / 2 - 30,
                };
                string labelText = point.DateTime.ToString("HH:mm");
                canvas.DrawText(labelText, label, labelPaint);
                SKPoint labelImage = new SKPoint
                {
                    X = x - 15,
                    Y = height - marginY / 2 - 40,
                };
                SKBitmap image = point.CurrentWeatherIcon;
                canvas.DrawBitmap(image, labelImage);

                // Draw and write max temperature point
                x += 25;
                double maxY = getTemperatureYCoord(height, marginY, MAX_TEMPERATURE, MIN_TEMPERATURE, double.Parse(point.MaxTemperature));
                canvas.DrawCircle(x, (float)maxY, 10, whitePaint);
                SKPoint labelTempPoint = new SKPoint
                {
                    X = x - 15,
                    Y = (float)(maxY - 30)
                };
                string labelTemp = point.MaxTemperature + "ºC";
                canvas.DrawText(labelTemp, labelTempPoint, labelPaint);

                // Draw and write min temperature point
                double minY = getTemperatureYCoord(height, marginY, MAX_TEMPERATURE, MIN_TEMPERATURE, double.Parse(point.MinTemperature));
                canvas.DrawCircle(x, (float)minY, 10, whitePaint);
                // if min and max temperature are different, write min temperature label
                if (double.Parse(point.MinTemperature) != double.Parse(point.MaxTemperature))
                {
                    labelTempPoint = new SKPoint
                    {
                        X = x - 15,
                        Y = (float)(minY + 50)
                    };
                    labelTemp = point.MinTemperature + "ºC";
                    canvas.DrawText(labelTemp, labelTempPoint, labelPaint);
                }
                
                if (n == 0)
                {
                    graphLinesMax.MoveTo(x, (float)maxY);
                    graphLinesMin.MoveTo(x, (float)minY);
                }
                else
                {
                    graphLinesMax.LineTo(x, (float)maxY);
                    graphLinesMin.LineTo(x, (float)minY);
                }

                n += 1;
            }

            canvas.DrawPath(graphLinesMax, maxStroke);
            canvas.DrawPath(graphLinesMin, minStroke);

            #endregion

        }

        private static double getTemperatureYCoord(int height, int marginY, double maxValue, double minValue, double temperature)
        {
            double usableHeight = height - marginY * 2;
            double range = maxValue - minValue;

            return usableHeight - (float)(temperature - minValue) * usableHeight / range + marginY;
        }
    }
}
