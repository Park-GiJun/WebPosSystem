<template>
  <div class="w-full h-full">
    <ApexChart
        type="line"
        :options="chartOptions"
        :series="getSeries"
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
  },
  type: {
    type: String,
    required: true,
    default: 'revenue'
  }
})

const getSeries = computed(() => [{
  name: props.type === 'revenue' ? '매출액' :
      props.type === 'quantity' ? '판매량' : '고객수',
  data: props.data.map(d => d[props.type])
}])

const chartOptions = {
  chart: {
    type: 'line',
    toolbar: {
      show: false
    },
    animations: {
      enabled: true,
      easing: 'smooth',
      speed: 800
    }
  },
  stroke: {
    curve: 'smooth',
    width: 3
  },
  colors: ['#4F46E5'],
  grid: {
    borderColor: '#f1f1f1',
    strokeDashArray: 4,
    padding: {
      top: 0,
      right: 0,
      bottom: 0,
      left: 10
    }
  },
  markers: {
    size: 4,
    colors: ['#4F46E5'],
    strokeColors: '#fff',
    strokeWidth: 2,
    hover: {
      size: 6
    }
  },
  xaxis: {
    categories: props.data.map(d => d.date),
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      }
    },
    axisBorder: {
      show: false
    },
    axisTicks: {
      show: false
    }
  },
  yaxis: {
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      },
      formatter: (value) => {
        if (props.type === 'revenue') {
          return value.toLocaleString() + '원'
        }
        return value.toLocaleString()
      }
    }
  },
  tooltip: {
    x: {
      show: true
    },
    y: {
      formatter: (value) => {
        if (props.type === 'revenue') {
          return value.toLocaleString() + '원'
        }
        return value.toLocaleString() + (props.type === 'quantity' ? '개' : '명')
      }
    }
  },
  annotations: {
    yaxis: [{
      y: 0,
      strokeDashArray: 0,
      borderColor: '#cbd5e1',
      borderWidth: 1
    }]
  }
}
</script>