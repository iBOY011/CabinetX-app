# PowerShell script to start all CabinetX microservices
# Order: configuration-service -> discovery-service -> gateway-service -> other services

param(
    [switch]$NoWait,
    [switch]$Verbose
)

# Colors for output
$Green = "Green"
$Red = "Red"
$Yellow = "Yellow"
$Cyan = "Cyan"
$White = "White"

function Write-ColorOutput {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

Write-ColorOutput "🚀 Starting CabinetX Microservices..." $Cyan
Write-ColorOutput "=====================================" $Cyan

# Array to track service status
$services = @(
    @{Name="configuration-service"},
    @{Name="discovery-service"},
    @{Name="gateway-service"},
    @{Name="analytics-service"},
    @{Name="appointment-service"},
    @{Name="billing-service"},
    @{Name="chatbot-service"},
    @{Name="clinic-service"},
    @{Name="consultation-service"},
    @{Name="medical-record-service"},
    @{Name="medication-service"},
    @{Name="notification-service"},
    @{Name="patient-service"},
    @{Name="payment-service"},
    @{Name="prescription-service"},
    @{Name="queue-service"},
    @{Name="user-service"}
)

# Function to check if a service is running on a port
function Test-ServiceRunning {
    param(
        [string]$ServiceName
    )

    # Check if process is running
    $process = Get-Process -Name "*java*" -ErrorAction SilentlyContinue |
        Where-Object { $_.CommandLine -like "*$ServiceName*" }
    if ($process) {
        Write-ColorOutput "✅ $ServiceName is running" $Green
        return $true
    } else {
        Write-ColorOutput "❌ $ServiceName is not running" $Red
        return $false
    }
}

# Function to start a service
function Start-ServiceProcess {
    param(
        [string]$ServiceName
    )

    Write-ColorOutput "🔄 Starting $ServiceName..." $Yellow

    $servicePath = Join-Path $PSScriptRoot $ServiceName
    if (Test-Path $servicePath) {
        $logPath = Join-Path $PSScriptRoot "logs"
        if (!(Test-Path $logPath)) {
            New-Item -ItemType Directory -Path $logPath | Out-Null
        }

        $logFile = Join-Path $logPath "$ServiceName.log"
        $startInfo = New-Object System.Diagnostics.ProcessStartInfo
        $startInfo.FileName = "mvn"
        $startInfo.Arguments = "spring-boot:run"
        $startInfo.WorkingDirectory = $servicePath
        $startInfo.UseShellExecute = $false
        $startInfo.RedirectStandardOutput = $true
        $startInfo.RedirectStandardError = $true
        $startInfo.CreateNoWindow = $true

        $process = New-Object System.Diagnostics.Process
        $process.StartInfo = $startInfo

        # Redirect output to log file
        $outputWriter = New-Object System.IO.StreamWriter $logFile
        $errorWriter = New-Object System.IO.StreamWriter $logFile, $true

        $process.OutputDataReceived += {
            param($sender, $e)
            if ($e.Data) {
                $outputWriter.WriteLine($e.Data)
            }
        }
        $process.ErrorDataReceived += {
            param($sender, $e)
            if ($e.Data) {
                $errorWriter.WriteLine($e.Data)
            }
        }

        $process.Start() | Out-Null
        $process.BeginOutputReadLine()
        $process.BeginErrorReadLine()

        if ($Verbose) {
            Write-ColorOutput "   Process ID: $($process.Id)" $White
        }

        # Store process info for later cleanup
        $script:runningProcesses += @{
            Name = $ServiceName
            Process = $process
            OutputWriter = $outputWriter
            ErrorWriter = $errorWriter
        }

        Start-Sleep -Seconds 5  # Wait a bit for service to start
    } else {
        Write-ColorOutput "❌ Directory $ServiceName not found" $Red
    }
}

# Initialize array to track running processes
$runningProcesses = @()

# Create logs directory
$logsPath = Join-Path $PSScriptRoot "logs"
if (!(Test-Path $logsPath)) {
    New-Item -ItemType Directory -Path $logsPath | Out-Null
}

# Start services in order
Write-ColorOutput "`n📋 Starting core services first..." $Cyan
Write-ColorOutput "" $White

# 1. Configuration Service
Start-ServiceProcess "configuration-service"

# 2. Discovery Service
Start-ServiceProcess "discovery-service"

# 3. Gateway Service
Start-ServiceProcess "gateway-service"

Write-ColorOutput "`n📋 Starting business services..." $Cyan
Write-ColorOutput "" $White

# 4. Other services
Start-ServiceProcess "analytics-service"
Start-ServiceProcess "appointment-service"
Start-ServiceProcess "billing-service"
Start-ServiceProcess "chatbot-service"
Start-ServiceProcess "clinic-service"
Start-ServiceProcess "consultation-service"
Start-ServiceProcess "medical-record-service"
Start-ServiceProcess "medication-service"
Start-ServiceProcess "notification-service"
Start-ServiceProcess "patient-service"
Start-ServiceProcess "payment-service"
Start-ServiceProcess "prescription-service"
Start-ServiceProcess "queue-service"
Start-ServiceProcess "user-service"

Write-ColorOutput "`n⏳ Waiting for all services to fully start..." $Yellow
Start-Sleep -Seconds 30

Write-ColorOutput "`n🔍 Checking service status..." $Cyan
Write-ColorOutput "==============================" $Cyan

# Check all services
$failedServices = @()
foreach ($service in $services) {
    if (!(Test-ServiceRunning $service.Name)) {
        $failedServices += $service.Name
    }
}

Write-ColorOutput "`n📊 Final Report" $Cyan
Write-ColorOutput "===============" $Cyan

if ($failedServices.Count -eq 0) {
    Write-ColorOutput "🎉 All services are running successfully!" $Green
} else {
    Write-ColorOutput "⚠️  The following services failed to start:" $Red
    foreach ($service in $failedServices) {
        Write-ColorOutput "   - $service" $Red
    }
    Write-ColorOutput "" $White
    Write-ColorOutput "💡 Check the logs in the 'logs/' directory for more details:" $White
    foreach ($service in $failedServices) {
        Write-ColorOutput "   - logs\$service.log" $White
    }
}

Write-ColorOutput "" $White
Write-ColorOutput "🔗 Useful URLs:" $Cyan
Write-ColorOutput "   - Eureka Dashboard: http://localhost:8761" $White
Write-ColorOutput "   - Config Server: http://localhost:8888" $White
Write-ColorOutput "   - Gateway: http://localhost:8080" $White

if ($NoWait) {
    Write-ColorOutput "`n🔄 Services are running in background. Press Ctrl+C to stop all services." $Yellow
    Write-ColorOutput "📝 To stop all services manually: Get-Process -Name '*java*' | Where-Object { `$_.CommandLine -like '*spring-boot:run*' } | Stop-Process" $White

    # Wait for user input to keep script running
    try {
        [void][Console]::ReadKey($true)
    } catch {
        # Handle interruption gracefully
    }
} else {
    Write-ColorOutput "`n📝 To stop all services: Get-Process -Name '*java*' | Where-Object { `$_.CommandLine -like '*spring-boot:run*' } | Stop-Process" $White
}

# Cleanup - close log writers
foreach ($procInfo in $runningProcesses) {
    if ($procInfo.OutputWriter) {
        $procInfo.OutputWriter.Close()
    }
    if ($procInfo.ErrorWriter) {
        $procInfo.ErrorWriter.Close()
    }
}