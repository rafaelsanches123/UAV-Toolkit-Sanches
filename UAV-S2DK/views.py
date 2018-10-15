#Authors: Jesimar da Silva Arantes and Andre Missaglia
#Date: 01/06/2017
#Last Update: 15/03/2018
#Description: Code that defines the commands that can be requested to the drone through the GET and POST methods.
#Descricao: Codigo que define os comandos que podem ser requisitados ao drone atraves dos metodos GET e POST.

from dronekit import VehicleMode
import commands
import time

'''
Comando que captura as informacoes do GPS.
'''
def getGPS(request):
    vehicle = request['vehicle']
    gps = vehicle.location.global_relative_frame
    return {
        'gps': [gps.lat, gps.lon]
    }

'''
Comando que captura as informacoes do barometro.
'''
def getBarometer(request):
    vehicle = request['vehicle']
    alt_rel = vehicle.location.global_relative_frame.alt
    alt_abs = vehicle.location.global_frame.alt
    return {
        'barometer': [alt_rel, alt_abs]
    }

'''
Comando que obtem informacoes da bateria do drone.
OBS: Necessita-se de power module para funcionar em drone real.
     Em simulacoes SITL funciona normalmente
'''
def getBattery(request):
    vehicle = request['vehicle']
    bat = vehicle.battery
    return {
        'bat': [bat.voltage, bat.current, bat.level]
    }

'''
Comando que obtem informacoes de pitch, yaw e roll.
'''
def getAttitude(request):
    vehicle = request['vehicle']
    att = vehicle.attitude
    return {
        'att': [att.pitch, att.yaw, att.roll]
    }

'''
Comando que obtem as informacoes de velocidade do drone.
'''
def getVelocity(request):
    vehicle = request['vehicle']
    return {
        'vel': vehicle.velocity
    }

'''
Comando que obtem a informacao de orientacao do drone.
'''
def getHeading(request):
    vehicle = request['vehicle']
    return {
        'heading': vehicle.heading
    }

'''
Comando que obtem a informacao de velocidade em relacao ao solo.
'''
def getGroundSpeed(request):
    vehicle = request['vehicle']
    return {
        'groundspeed': vehicle.groundspeed
    }

'''
Comando que obtem a informacao de velocidade em relacao ao ar.
'''
def getAirSpeed(request):
    vehicle = request['vehicle']
    return {
        'airspeed': vehicle.airspeed
    }

'''
Comando que obtem informacoes do GPS.
  GPSInfo.fix_type           -> no fix, 2: 2D fix, 3: 3D fix
  GPSInfo.satellites_visible -> numero de satelites visiveis.
  GPSInfo.eph                -> GPS horizontal dilution of position (HDOP).
  GPSInfo.epv                -> GPS vertical   dilution of position (VDOP).
'''
def getGPSInfo(request):
    vehicle = request['vehicle']
    return {
        'gpsinfo': [vehicle.gps_0.fix_type, vehicle.gps_0.satellites_visible, vehicle.gps_0.eph, vehicle.gps_0.epv]
    }

'''
Comando que obtem informacoes do modo de voo.
'''
def getMode(request):
    vehicle = request['vehicle']
    return {
        'mode': vehicle.mode.name
    }

'''
Comando que obtem informacoes do status de voo.
'''
def getSystemStatus(request):
    vehicle = request['vehicle']
    return {
        'system-status': vehicle.system_status.state
    }

'''
Comando que obtem informacoes se o drone esta armado.
'''
def getArmed(request):
    vehicle = request['vehicle']
    return {
        'armed': vehicle.armed
    }

'''
Comando que obtem informacoes se o drone eh armavel.
'''
def getIsArmable(request):
    vehicle = request['vehicle']
    return {
        'is-armable': vehicle.is_armable
    }

'''
Comando que obtem informacoes se o piloto automatico possui (Filtro de Kalman Estendido) EKF.
'''
def getEkfOk(request):
    vehicle = request['vehicle']
    return {
        'ekf-ok': vehicle.ekf_ok
    }

'''
Comando que obtem informacoes do proximo waypoint a ser seguido pelo drone.
'''
def getNextWaypoint(request):
    vehicle = request['vehicle']
    return {
        'next-waypoint': vehicle.commands.next
    }

'''
Comando que obtem informacoes da quantidade total de waypoints carregados no AP.
'''
def getCountWaypoint(request):
    vehicle = request['vehicle']
    return {
        'count-waypoint': vehicle.commands.count
    }

'''
Comando que obtem a localizacao do home (home-location).
Nota: O download do home-location eh necessario porque o ArduPilot 
implementa/armazena o home-location como um waypoint em vez de envia-lo como mensagens.
'''
#Nota: as vezes da alguns problemas devido ao uso do download() e wait_ready()
#Em geral esses problemas nao levam a queda do drone (problema nao critico)
def getHomeLocation(request):
    vehicle = request['vehicle']
    cmds = vehicle.commands
    cmds.download()
    cmds.wait_ready()
    #lat = 0.0
    #lon = 0.0
    #alt = 0.0
    #if vehicle.home_location is not None:
    #    lat = 0.0
    #    lon = 0.0
    #    alt = 0.0
    return {
        'home-location': [vehicle.home_location.lat, vehicle.home_location.lon, vehicle.home_location.alt]
    }

'''
Comando que obtem informacoes dos parametros do piloto automatico.
'''
def getParameters(request):
    vehicle = request['vehicle']
    param = ""
    for key, value in vehicle.parameters.iteritems():
        param += "Key:%s Value:%s; " % (key,value)
    return {
        'parameters': param
    }

'''
Comando que obtem a distancia do drone ate o home-location.
'''
def getDistanceToHome(request):
    vehicle = request['vehicle']
    posAtual = vehicle.location.global_relative_frame
    return {
        'distance-to-home': commands.getDistanceMeters(posAtual, vehicle.home_location)
    }

'''
Comando que obtem a distancia do drone para o waypoint a ser buscado (atual).
'''
def getDistanceToWptCurrent(request):
    vehicle = request['vehicle']
    return {
        'distance-to-wpt-current': commands.getDistanceToCurrentWaypoint(vehicle)
    }

'''
Comando que obtem todas as informacoes dos sensores do drone em um unico comando.
'''
def getAllInfoSensors(request):
    vehicle = request['vehicle']
    gps = vehicle.location.global_relative_frame
    alt_abs = vehicle.location.global_frame.alt
    bat = vehicle.battery
    att = vehicle.attitude
    dist_to_home = commands.getDistanceMeters(gps, vehicle.home_location)
    dist_to_current_waypoint = commands.getDistanceToCurrentWaypoint(vehicle)
    next_waypoint = vehicle.commands.next
    count_waypoint = vehicle.commands.count
    print 'next-waypoint = %s    count-waypoint = %s' % (next_waypoint, count_waypoint)
    print 'dist-to-home: ', dist_to_home
    print 'dist-to-current-wpt (%s): %s' % (next_waypoint, dist_to_current_waypoint)
    return {
        'all-sensors': [float('%.10g' % gps.lat), float('%.10g' % gps.lon), 
        float('%.2g' % gps.alt), float('%.2g' % alt_abs), float('%.4g' % att.pitch), 
        float('%.4g' % att.yaw), float('%.4g' % att.roll), vehicle.heading, 
        float('%.2g' % vehicle.groundspeed), float('%.2g' % vehicle.airspeed), 
        vehicle.gps_0.fix_type, vehicle.gps_0.satellites_visible, 
        vehicle.gps_0.eph, vehicle.gps_0.epv, vehicle.velocity, next_waypoint, 
        count_waypoint, dist_to_home, dist_to_current_waypoint, vehicle.mode.name, 
        vehicle.system_status.state, vehicle.armed, vehicle.is_armable, vehicle.ekf_ok]
    }

'''
Comando que define novas valores para os parametros do piloto automatico.
'''
def setParameter(request):
    parameter = request['body']['parameter']
    vehicle = request['vehicle']
    key = parameter['key'].encode()
    value = parameter['value']
    vehicle.parameters[key]=value
    return {
        'status-set-parameter': 'ok'
    }

'''
Comando que troca o modo de voo do piloto automatico.
'''
def setMode(request):
    vehicle = request['vehicle']
    mode = request['body']
    while vehicle.mode != VehicleMode(mode):
        vehicle.mode = VehicleMode(mode)
        time.sleep(0.5)
    return True

'''
Comando que define a velocidade limite de voo.
Os valores permitidos devem estar dentro do intervalo dado a seguir.
0 m/s < velocidade < 20 m/s
Valores fora sao ignorados.
'''
def setVelocity(request):
    velocity = request['body']
    vehicle = request['vehicle']
    vehicle.groundspeed = velocity
    vehicle.airspeed = velocity
    return {
        'status-set-velocity': 'ok'
    }

'''
Comando que limpa a missao atual e adiciona um waypoint objetivo na aeronave
'''
def setWaypoint(request):
    waypoint = request['body']['waypoint']
    vehicle = request['vehicle']
    cmds = vehicle.commands
    cmds.clear()
    cmds.next = 0
    if waypoint['action'] == 'takeoff':
        commands.takeoff(vehicle, waypoint['alt'], cmds)
    elif waypoint['action'] == 'goto':
        commands.goto(waypoint['lat'], waypoint['lng'], waypoint['alt'], cmds)
    elif waypoint['action'] == 'land':
        commands.land(waypoint['lat'], waypoint['lng'], cmds)
    elif waypoint['action'] == 'landv':
        commands.landVertical(cmds)
    elif waypoint['action'] == 'rtl':
        commands.rtl(cmds)
    cmds.upload()
    commands.startmission(vehicle) #comando necessario caso o veiculo esteja no solo
    return {
        'status-set-waypoint': 'ok'
    }

#Nao funciona sempre: so consegui ver isso com software de Voice-Commands.
#Acredito que exista algum problema nos comandos cmds.download() e cmds.wait_ready().
#Adiciona um waypoint objetivo ao final da missao corrente (append) na aeronave
#aqui existe um problema com o cmds.next. este problema ocorre quando a aeronave 
#ja havia cumprido todos os waypoints, por exemplo, next waypoint=16
#e count waypoint=16 e entao a rota eh atualizada. dessa forma o count waypoint=31 
#e entao o next waypoint passa a valer 0. Mesmo atualizando o next para o waypoint 
#correto a aeronave nao segue a rota. Acredito que tenha que dar mais algum comando
#para que ela siga de fato o waypoint do next.

'''
Conclusoes: Esta funcao apresenta dois problemas: 
Problema 1: As vezes da erro no metodo cmds.download() e cmds.wait_ready()
Problema 2: Caso esta funcao seja chamada quando a aeronave ja tiver cumprido
todo o plano de voo (mesmo que ela estaja no ar) nao se pode chamar essa funcao.
'''
def appendWaypoint(request):
    waypoint = request['body']['waypoint']
    vehicle = request['vehicle']
    cmds = vehicle.commands
    cmds.download()
    cmds.wait_ready()
    if waypoint['action'] == 'takeoff':
        commands.takeoff(vehicle, waypoint['alt'], cmds)
    elif waypoint['action'] == 'goto':
        commands.goto(waypoint['lat'], waypoint['lng'], waypoint['alt'], cmds)
    elif waypoint['action'] == 'land':
        commands.land(waypoint['lat'], waypoint['lng'], cmds)
    elif waypoint['action'] == 'landv':
        commands.landVertical(cmds)
    elif waypoint['action'] == 'rtl':
        commands.rtl(cmds)
    cmds.upload()
    commands.startmission(vehicle) #comando necessario caso o veiculo esteja no solo
    #if cmds.next == 0: #estes comandos estao sendo testados para tratar o problema 2.
    #    print 'next = 0'
    #    cmds.next = cmds.count
    #if cmds.next == cmds.count - 1:
    #    print 'next = count -1'
    #    cmds.next = cmds.count
    return {
        'status-append-waypoint': 'ok'
    }

'''
Este comando limpa a missao atual e adiciona uma missao nova na aeronave
'''
def setMission(request):
    mission = request['body']['mission']
    vehicle = request['vehicle']
    cmds = vehicle.commands
    cmds.clear()
    cmds.next = 0
    for item in mission:
        if item['action'] == 'takeoff':
            commands.takeoff(vehicle, item['alt'], cmds)
        elif item['action'] == 'goto':
            commands.goto(item['lat'], item['lng'], item['alt'], cmds)
        elif item['action'] == 'land':
            commands.land(item['lat'], item['lng'], cmds)
        elif item['action'] == 'landv':
            commands.landVertical(cmds)
        elif item['action'] == 'rtl':
            commands.rtl(cmds)
    cmds.upload()
    commands.startmission(vehicle) #comando necessario caso o veiculo esteja no solo
    return {
        'status-set-mission': 'ok'
    }

'''
#Adiciona uma missao ao final da missao corrente (append) na aeronave
Conclusoes: Esta funcao apresenta dois problemas: 
Problema 1: As vezes da erro no metodo cmds.download() e cmds.wait_ready()
Problema 2: Caso esta funcao seja chamada quando a aeronave ja tiver cumprido
todo o plano de voo (mesmo que ela estaja no ar) nao se pode chamar essa funcao.
Este problema  eh o mesmo do appendWaypoint().
'''
def appendMission(request):
    mission = request['body']['mission']
    vehicle = request['vehicle']
    cmds = vehicle.commands
    cmds.download()
    cmds.wait_ready()
    for item in mission:
        if item['action'] == 'takeoff':
            commands.takeoff(vehicle, item['alt'], cmds)
        elif item['action'] == 'goto':
            commands.goto(item['lat'], item['lng'], item['alt'], cmds)
        elif item['action'] == 'land':
            commands.land(item['lat'], item['lng'], cmds)
        elif item['action'] == 'landv':
            commands.landVertical(cmds)
        elif item['action'] == 'rtl':
            commands.rtl(cmds)
    cmds.upload()
    commands.startmission(vehicle) #comando necessario caso o veiculo esteja no solo
    return {
        'status-append-mission': 'ok'
    }

'''
Aeronave deve estar deslocando (em movimento) caso contrario nao funciona
Angulo: apenas valores positivos (0 a 360)
'''
def setHeading(request):
    heading = request['body']['heading']
    vehicle = request['vehicle']
    angle = heading['value']
    if heading['typeDirection'] == 'ccw': #valores direction: ccw = -1, cw = 1
        direction = -1
    elif heading['typeDirection'] == 'cw':
        direction = 1
    if heading['typeAngle'] == 'absolute': #valores is_relative: relative = 1, absolute = 0
        is_relative = 0
    elif heading['typeAngle'] == 'relative':
        is_relative = 1
    commands.defineHeading(vehicle, angle, direction, is_relative)
    return {
        'status-set-heading': 'ok'
    }


#====================== Comando ainda nao usados =======================

"""
Este comando limpa a missao corrente.
"""
def clearMission(vehicle):
    cmds = vehicle.commands
    vehicle.commands.clear()
    vehicle.flush()
    # After clearing the mission you MUST re-download the mission from the vehicle
    # before vehicle.commands can be used again
    # (see https://github.com/dronekit/dronekit-python/issues/230)
    cmds = vehicle.commands
    cmds.download()
    cmds.wait_ready()

"""
Downloads the mission and returns the wp list and number of WP.
"""
def getCurrentMission(vehicle):
    print "Downloading mission"
    cmds = vehicle.commands
    cmds.download()
    cmds.wait_ready()
    missionList = []
    n_WP = 0
    for wp in vehicle.commands:
        missionList.append(wp)
        n_WP += 1
    return n_WP, missionList
