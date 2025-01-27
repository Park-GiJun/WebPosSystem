<template>
  <div class="w-full h-full">
    <ApexChart
        type="pie"
        :options="chartOptions"
        :series="series"
        height="100%"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ApexChart from 'vue3-apexcharts'

const props = defineProps({
  data: {
    type: Array,
    required: true,
    default: () => []
  }
})

const series = computed(() => props.data.map(d => d.value))

const chartOptions = {
  chart: {
    type: 'pie',
    animations: {
      enabled: true,
      speed: 500,
      animateGradually: {
        enabled: true,
        delay: 150
      },
      dynamicAnimation: {
        enabled: true,
        speed: 350
      }
    }
  },
  labels: props.data.map(d => d.name),
  colors: ['#4F46E5', '#10B981', '#F59E0B', '#EF4444'],
  legend: {
    position: 'bottom',
    fontSize: '14px',
    markers: {
      width: 12,
      height: 12,
      radius: 12
    }
  },
  stroke: {
    width: 0
  },
  dataLabels: {
    enabled: true,
    formatter: function(val) {
      return val.toFixed(1) + '%'
    },
    style: {
      fontSize: '12px',
      fontFamily: 'inherit',
      fontWeight: 'medium'
    },
    dropShadow: {
      enabled: false
    }
  },
  tooltip: {
    y: {
      formatter: function(val) {
        return val + '%'
      }
    }
  },
  plotOptions: {
    pie: {
      donut: {
        size: '0%'
      }
    }
  }
}
</script>