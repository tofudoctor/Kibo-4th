# Kibo RPC Tutorial Videos Summary

- Start mission : bool result = api.startMission
- Move astrobee : api.moveTo
    - know the system configuration of Astrobee keyword -> Astrobee Robot Software
- Turn on the laser : api.laserControl(true)
    - know the system configuration of Astrobee keyword -> Astrobee Robot Software
- Movement and Rotation
    - keyword : LVLH attitude
    - the coordinate system is z-axis in opposite way
    - unit : meter
    - function to rotate : use quatertions and rotation
    - for why : quaternions rotation vector
    - not rotating : face X axis plus direction
    - how to rotate : quaternions tool (recommand)
- When the Astrobee arrive point 1, call api.reportPoint1Arrival()
- using NavCam shoot image : api.getMatNavCam() -> recognize AR tags
    - save : api.saveMatImage()
- recognize AR tags : keyword opencv aruco
- retrieves snapshot of Target 1 : api.takeTarget1Snapshot()
    - used to determine what part is hit when a laser was shot at Target1
- Mission complete : api.reportMissionCompletion()
    - communicates the astronauts that the mission is complete
- get the activate target : api.getActiveTargets()
## Log
:::warning
logs help us to analyze the cause
:::

- Log.i(first arg.,second arg.)
    - first : tag, class name, etc.
    - second : anything want to see in the logs
    - to check the message : .log file -> find I/YourService



## Programming Tips
- Try adding various parameters, including your own coordinates, coordinates of KOZ (Keep Out Zone), remaining time to the condition.
## Tend to happen
- Null Point Exception
- Index Out Bounds Exception

## References
![](https://i.imgur.com/DjIaEe1.jpg)

![](https://i.imgur.com/YFfurjh.png)

![](https://i.imgur.com/NINS3EO.png)

![](https://i.imgur.com/CYgJbQc.png)

![](https://i.imgur.com/vgkGmji.png)

![](https://i.imgur.com/siQ28Bj.jpg)

![](https://i.imgur.com/MEUut2w.jpg)

![](https://i.imgur.com/RLrdXSU.jpg)

⚫ GitHub-1 ( https://github.com/nasa/astrobee )

⚫ GitHub-2 ( https://github.com/nasa/astrobee_android )

⚫ Website of Astrobee ( https://www.nasa.gov/astrobee )
