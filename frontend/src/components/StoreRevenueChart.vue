<!-- StoreRevenueChart.vue -->
<template>
  <div class="w-full h-full">
    <ApexChart
        type="bar"
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

const series = computed(() => [{
  name: '매출액',
  data: props.data.map(d => d.value)
}])

const chartOptions = {
  chart: {
    type: 'bar',
    toolbar: {
      show: false
    }
  },
  plotOptions: {
    bar: {
      borderRadius: 6,
      horizontal: true,
      dataLabels: {
        position: 'top'
      }
    }
  },
  colors: ['#4F46E5'],
  dataLabels: {
    enabled: true,
    formatter: val => val.toLocaleString() + '원',
    offsetX: 30,
    style: {
      fontSize: '12px',
      fontFamily: 'inherit'
    }
  },
  xaxis: {
    categories: props.data.map(d => d.name),
    labels: {
      style: {
        fontSize: '12px',
        fontFamily: 'inherit'
      }
    }
  },
  yaxis: {
    labels: {
      formatter: value => value.toLocaleString() + '원'
    }
  }
}
</script>