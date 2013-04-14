import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
from pylab import polyfit
from mpl_toolkits.mplot3d import Axes3D

from polynomial_tools import PolyFit

"""
Created on Apr 14, 2013

@author: Doug Szumski, Will Szumski

A tool to map speed, resistance and power for the Bushido 

Assumes data in the format of the example calibration file

Prints coefficients which are used in JTurbo and plots the raw data and surface fit 

"""
def getCalibrationData(filename):
    """
    Reads calibration data in, creating nested lists for each fixed resistance. 
    Each of these lists contains measurements of speed and power
    """
    resistance_series = []
    speed_power_measurement = []
    c_res = 0.0
    with open(filename) as f:
        for line in f:
            line = line.split(";")
            try:
                reading = []
                for item in line:
                    reading.append(float(item))
                if (reading[2] != c_res):
                    c_res = reading[2]
                    resistance_series.append(speed_power_measurement)
                    speed_power_measurement = []
                speed_power_measurement.append(reading)
            except ValueError:
                pass
    return resistance_series

def unzipCalibrationData(resistance_series):
    """
    De-nests resistances series into lists of power, 
    speed and resistance
    """
    speed = []
    power = []
    resistance = []
    for speed_power_measurement in resistance_series:
        for reading in speed_power_measurement:
            speed.append(reading[0])
            power.append(reading[1])
            resistance.append(reading[2])
    return np.array(speed), np.array(power), np.array(resistance)
            

def linFitData(resistance_series, max_power = 1000, min_power = 0, max_speed =65):
    """
    Fits linear regression lines to resistance series, and returns
    points extrapolated across the range of powers between
    min_power and MaxPower. 
    
    Speed > max_speed are filtered to get rid of non-linear behaviour
    """
    speed = []
    power = []
    resistance = []
    for data_list in resistance_series:
        speed_data = []
        power_data = []
        #Extract the current resistance level for the series
        res_level = data_list[0][2]
        #Iterate over the speed, power readings in the series
        for item in data_list:
            #Filter out high speeds with non-linear behaviour
            if (item[0] < max_speed):
                speed_data.append(item[0])
                power_data.append(item[1])
        #Fit linear regression lines to each speed-power series
        m,c = polyfit(power_data, speed_data, 1)
        #Evaluate data points cross linear regression line
        powerFit = np.arange(min_power, max_power, 10)
        speedFit = np.polyval([m,c], powerFit)
        #Synthesis 'linear fitted' list of speed, power measurements
        for i in range(len(powerFit)):
            speed.append(speedFit[i])
            power.append(powerFit[i])
            resistance.append(res_level)
    return np.array(speed), np.array(power), np.array(resistance)

# Read the calibration data in
data = getCalibrationData("exampleCalibrationData")

# Smooth the data by fitting linear regression lines to fixed
# resistances and extrapolating it
x,y,z = linFitData(data)

# Fit a 3rd order, 2d polynomial
pf = PolyFit()
m = pf.polyfit2d(x,y,z)
# Surface coeffs used Cyclismo
print "Surface fit coefficients: ", m

# Evaluate the surface over a grid of dimensions nx x ny
nx, ny = 250, 250

# Grid extent
min_speed = 0
max_speed = 100
min_power = 0
max_power = 1000

xx, yy = np.meshgrid(np.linspace(min_speed, max_speed, nx), 
                     np.linspace(min_power, max_power, ny))
zz = pf.polyval2d(xx, yy, m)

#Bound the resistance values on the grid between these brake resistances:
max_res = 1000
min_res = +250
for i in range(len(zz)):
    for j in range(len(zz[i])):
        if (zz[i,j] > max_res):
             zz[i,j] = max_res
        elif (zz[i,j] < min_res):
            zz[i,j] = min_res
            
#Get a copy of the raw data for comparison
xs, ys, zs = unzipCalibrationData(data)

# Visualise the data
fig = plt.figure()
ax1 = plt.subplot2grid((2, 2), (0, 0))
ax2 = plt.subplot2grid((2, 2), (0, 1), projection='3d')
ax3 = plt.subplot2grid((2, 2), (1, 0), projection='3d')
ax4 = plt.subplot2grid((2, 2), (1, 1), projection='3d')

# Colour map colour scheme and limits
cm = plt.cm.get_cmap('jet')
cmin = 200
cmax = 1000

# Plot 1
ax1.set_title('Raw calibration data and bounded surface fit')
ax1.set_xlabel("Speed (km/h)")
ax1.set_ylabel("Power (W)")
ax1.set_xlim(0,100)
ax1.set_ylim(0,1000)
surf = ax1.pcolor(xx, yy, zz, cmap=cm,  vmin = cmin, vmax = cmax)
ax1.scatter(xs, ys, c=zs, cmap=cm,  vmin = cmin, vmax = cmax)

# Plot 2
ax2.set_title('Bounded surface fit')
ax2.plot_surface(xx, yy, zz, cmap=cm, linewidth=0.2, alpha = 1.0,   vmin = cmin, vmax = cmax)
ax2.set_xlabel("Speed (km/h)")
ax2.set_ylabel("Power (W)")
ax2.set_zlabel("Resistance (arb. units)")

# Plot 3
ax3.set_title('Raw surface')
ax3.plot_trisurf(xs, ys, zs, cmap=cm, linewidth=0.2,   vmin = cmin, vmax = cmax)
ax3.set_zlim(-200,600)
ax3.set_xlabel("Speed (km/h)")
ax3.set_ylabel("Power (W)")
ax3.set_zlabel("Resistance (arb. units)")

# Plot 4
ax4.set_title('Raw data overlayed onto surface fit')
ax4.plot_surface(xx, yy, zz, cmap=cm, linewidth=0.2, alpha = 0.3,   vmin = cmin, vmax = cmax)
ax4.scatter(xs, ys, zs, c=zs, cmap=cm,   vmin = cmin, vmax = cmax)
ax4.set_zlim(-250,1000)
ax4.set_xlabel("Speed (km/h)")
ax4.set_ylabel("Power (W)")
ax4.set_zlabel("Resistance (arb. units)")

# Plot colour bar
fig.subplots_adjust(bottom=0.2)
cbar_ax = fig.add_axes([0.12, 0.05, 0.8, 0.05]) 
cbar_ax.set_title("Brake resistance (arb. units)")
fig.colorbar(surf, cax=cbar_ax,  orientation='horizontal')

#Save and show figure
#plt.savefig("plot.png",figsize=(10,6))
plt.show()
