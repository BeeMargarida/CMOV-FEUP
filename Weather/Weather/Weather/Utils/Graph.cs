using System;
using System.Collections.Generic;
using SkiaSharp;
using SkiaSharp.Views.Forms;
using Weather.Models;

namespace Weather.Utils
{
    class Graph
    {
        public static void Draw(SKPaintSurfaceEventArgs e, Status day)
        {
            SKCanvas canvas = e.Surface.Canvas;

            int width = e.Info.Width;
            int height = e.Info.Height;
            int marginX = (int)(width * 0.1);
            int marginY = (int)(height * 0.20);
            double maxTemperature = getMaxTemperature(day.GraphData);
            double minTemperature = getMinTemperature(day.GraphData);

            // Paint background
            canvas.Clear(SKColors.Cyan);

            #region Draw Axis
            SKPaint axisPaint = new SKPaint
            {
                Style = SKPaintStyle.Stroke,
                Color = SKColors.Black,
                StrokeWidth = 2,
            };

            SKPath axisLines = new SKPath();

            axisLines.MoveTo(marginX, marginY);
            axisLines.LineTo(marginX, height - marginY);
            axisLines.LineTo(width - marginX, height - marginY);

            canvas.DrawPath(axisLines, axisPaint);
            #endregion

            #region Draw Labels
            // Label Color
            SKPaint labelPaint = new SKPaint
            {
                Style = SKPaintStyle.Fill,
                Color = SKColors.Black,
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
                Color = SKColors.DarkRed,
                StrokeWidth = 5,
            };
            SKPaint minStroke = new SKPaint
            {
                Style = SKPaintStyle.Stroke,
                Color = SKColors.DarkBlue,
                StrokeWidth = 5,
            };

            SKPath graphLinesMax = new SKPath();
            SKPath graphLinesMin = new SKPath();

            int n = 0;
            // spacing between the hours, based on the number of points and the width of the screen
            int spacing = day.GraphData.Count == 2 ? width - 3*marginX:  (width - 2*marginX) / day.GraphData.Count;
            foreach(GraphData point in day.GraphData)
            {
                float x = marginX + (float)n * spacing;
                float y = height - marginY / 2 - 20;
                SKPoint label = new SKPoint
                {
                    X = x,
                    Y = height - marginY / 2 - 20,
                };
                string labelText = point.DateTime.ToString("HH:mm");
                canvas.DrawText(labelText, label, labelPaint);
                SKPoint labelImage = new SKPoint
                {
                    X = x,
                    Y = height - marginY / 2 - 30,
                };
                SKBitmap image = point.CurrentWeatherIcon;
                canvas.DrawBitmap(image, labelImage);

                double maxY = getTemperatureYCoord(height, marginY, maxTemperature, minTemperature, double.Parse(point.MaxTemperature));
                canvas.DrawCircle(x, (float)maxY, 10, whitePaint);
                double minY = getTemperatureYCoord(height, marginY, maxTemperature, minTemperature, double.Parse(point.MinTemperature));
                canvas.DrawCircle(x, (float)minY, 10, whitePaint);
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

        private static double getMaxTemperature(List<GraphData> data)
        {
            double max = 0;
            foreach(GraphData entry in data)
            {
                if(Double.Parse(entry.MaxTemperature) > max)
                {
                    max = Double.Parse(entry.MaxTemperature);
                }
            }
            return max;
        }

        private static double getMinTemperature(List<GraphData> data)
        {
            double min = 100000;
            foreach (GraphData entry in data)
            {
                if (Double.Parse(entry.MinTemperature) < min)
                {
                    min = Double.Parse(entry.MinTemperature);
                }
            }
            return min;
        }

        private static double getTemperatureYCoord(int height, int marginY, double maxValue, double minValue, double temperature)
        {
            double usableHeight = height - marginY * 2;
            double range = maxValue - minValue;

            return usableHeight - (float)(temperature - minValue) * usableHeight / range + marginY;
        }
    }
}
